package org.antlr.jetbrains.st4plugin;

import com.intellij.ide.impl.StructureViewWrapperImpl;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.structureView.StructureViewFactoryEx;
import com.intellij.ide.structureView.StructureViewWrapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorFactoryAdapter;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBusConnection;
import org.antlr.jetbrains.st4plugin.structview.STGroupStructureViewModel;
import org.jetbrains.annotations.NotNull;

public class STGroupPluginController implements ProjectComponent {
	public static final int DELAY_BEFORE_STRUCTVIEW_REFRESH = 3000;

	public static final String PLUGIN_ID = "org.antlr.jetbrains.st4plugin";
	public static final Logger LOG = Logger.getInstance("STGroupPluginController");
	public static final Key<STGroupFileEditorListener> EDITOR_DOCUMENT_LISTENER_KEY =
		Key.create("EDITOR_DOCUMENT_LISTENER_KEY");
	public static final Key<DocumentListener> EDITOR_STRUCTVIEW_LISTENER_KEY =
		Key.create("EDITOR_STRUCTVIEW_LISTENER_KEY");

	public Project project;
	public boolean projectIsClosed = false;

	public MyVirtualFileListener myVirtualFileListener = new MyVirtualFileListener();
	public MyFileEditorManagerListener myFileEditorManagerListener = new MyFileEditorManagerListener();

	public STGroupPluginController(Project project) {
		this.project = project;
	}

	public static STGroupPluginController getInstance(Project project) {
		if ( project==null ) {
			LOG.error("getInstance: project is null");
			return null;
		}
		STGroupPluginController pc = project.getComponent(STGroupPluginController.class);
		if ( pc==null ) {
			LOG.error("getInstance: getComponent() for "+project.getName()+" returns null");
		}
		return pc;
	}

	@Override
	public void projectOpened() {
		IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId(PLUGIN_ID));
		String version = "unknown";
		if ( plugin!=null ) {
			version = plugin.getVersion();
		}
		LOG.info("StringTemplate 4 Plugin version "+version+", Java version "+ SystemInfo.JAVA_VERSION);

		installListeners();
	}

	@Override
	public void projectClosed() {
		LOG.info("projectClosed " + project.getName());
		//synchronized ( shutdownLock ) { // They should be called from EDT only so no lock
		projectIsClosed = true;
		project = null;
		uninstallListeners();
	}

	@Override
	public void initComponent() { }

	@Override
	public void disposeComponent() { }

	@NotNull
	@Override
	public String getComponentName() {
		return "st.ProjectComponent";
	}


	public void installListeners() {
		LOG.info("installListeners " + project.getName());
		// Listen for .stg file saves
		VirtualFileManager.getInstance().addVirtualFileListener(myVirtualFileListener);

		// Listen for editor window changes
		MessageBusConnection msgBus = project.getMessageBus().connect(project);
		msgBus.subscribe(
			FileEditorManagerListener.FILE_EDITOR_MANAGER,
			myFileEditorManagerListener
		);

		// Listen for editor creation and release so that we can install
		// keyboard listeners that notify us when to reanalyze the file.
		// listener should be removed by Intellij when project is disposed
		// per doc.
		EditorFactory factory = EditorFactory.getInstance();
		factory.addEditorFactoryListener(new MyEditorFactoryListener(), project);
	}

	// seems that intellij can kill and reload a project w/o user knowing.
	// a ptr was left around that pointed at a disposed project.
	// Probably was a listener still attached and triggering
	// editor listeners events.
	public void uninstallListeners() {
		VirtualFileManager.getInstance().removeVirtualFileListener(myVirtualFileListener);
		if ( !projectIsClosed ) {
			MessageBusConnection msgBus = project.getMessageBus().connect(project);
			msgBus.disconnect();
		}
	}

	public void fileSavedEvent(VirtualFile file) {
		LOG.info("fileSavedEvent " + (file != null ? file.getPath() : "none") + " " + project.getName());
	}

	public void currentEditorFileSwitchedEvent(VirtualFile oldFile, VirtualFile newFile) {
		LOG.info("currentEditorFileSwitchedEvent "+(oldFile!=null?oldFile.getPath():"none")+
				 " -> "+(newFile!=null?newFile.getPath():"none")+" "+project.getName());
		if ( newFile==null ) { // all files must be closed I guess
			return;
		}

		if ( newFile.getName().endsWith(".stg") ) {
			Document doc = FileDocumentManager.getInstance().getDocument(newFile);
			syntaxHighlightDocument(doc);
		}
	}

	public void editorDocumentAlteredEvent(Document doc) {
		syntaxHighlightDocument(doc);
	}

	public void syntaxHighlightDocument(Document doc) {
		final Editor editor = getEditor(doc);
		if ( editor==null ) return;
		ApplicationManager.getApplication().invokeLater(
			new Runnable() {
				@Override
				public void run() {
					XSTGroupSyntaxHighlighter groupHighlighter = new XSTGroupSyntaxHighlighter(editor,0);
					groupHighlighter.highlight();
				}
			});
	}

	public void editorFileClosedEvent(VirtualFile vfile) {
		// hopefully called only from swing EDT
		String fileName = vfile.getPath();
		LOG.info("editorFileClosedEvent "+fileName+" "+project.getName());
	}

	public Editor getEditor(Document doc) {
		if (doc == null) return null;

		EditorFactory factory = EditorFactory.getInstance();
		final Editor[] editors = factory.getEditors(doc, project);
		if ( editors.length==0 ) {
			// no editor found for this file. likely an out-of-sequence issue
			// where Intellij is opening a project and doesn't fire events
			// in order we'd expect.
			return null;
		}
		return editors[0]; // hope just one
	}

//	boolean structureViewRebuildRequested = false;
	long structureViewRebuildStarted = 0;

	public void registerStructureViewModel(final Editor editor, final STGroupStructureViewModel model) {
//		System.out.println("\nregisterStructureViewModel");
		final Document doc = editor.getDocument();
		final DocumentListener listener = new DocumentAdapter() {
			@Override
			public void documentChanged(DocumentEvent e) {
//				System.out.println("TRUCTVIEW_LISTENER triggered");
				final StructureViewWrapper viewWrapper =
					StructureViewFactoryEx.getInstanceEx(project).getStructureViewWrapper();
				if ( viewWrapper instanceof StructureViewWrapperImpl ) {
					long now = System.currentTimeMillis();
					if ( now-structureViewRebuildStarted >= DELAY_BEFORE_STRUCTVIEW_REFRESH ) {
						// done on GUI thread so access should be serialized
						structureViewRebuildStarted = now;
						ApplicationManager.getApplication().invokeLater(
							new Runnable() {
								@Override
								public void run() {
									((StructureViewWrapperImpl) viewWrapper).rebuild();
								}
							});
					}
				}
			}
		};
		DocumentListener oldListener = doc.getUserData(EDITOR_STRUCTVIEW_LISTENER_KEY);
		if ( oldListener!=null ) {
			doc.removeDocumentListener(oldListener);
		}
		doc.putUserData(EDITOR_STRUCTVIEW_LISTENER_KEY, listener);
		doc.addDocumentListener(listener);
	}

	// E v e n t  L i s t e n e r s

	private class MyVirtualFileListener extends VirtualFileAdapter {
		@Override
		public void contentsChanged(VirtualFileEvent event) {
			final VirtualFile vfile = event.getFile();
			if ( !vfile.getName().endsWith(".stg") ) return;
			if ( !projectIsClosed ) fileSavedEvent(vfile);
		}
	}

	private class MyFileEditorManagerListener extends FileEditorManagerAdapter {
		@Override
		public void selectionChanged(FileEditorManagerEvent event) {
			if ( !projectIsClosed ) {
				final VirtualFile vfile = event.getNewFile();
				if ( vfile!=null && vfile.getName().endsWith(".stg") ) {
					currentEditorFileSwitchedEvent(event.getOldFile(), event.getNewFile());
				}
			}
		}

		@Override
		public void fileClosed(FileEditorManager source, VirtualFile vfile) {
			if ( !projectIsClosed ) {
				if ( vfile!=null && vfile.getName().endsWith(".stg") ) {
					editorFileClosedEvent(vfile);
				}
			}
		}
	}

	private class STGroupFileEditorListener extends DocumentAdapter {
		@Override
		public void documentChanged(DocumentEvent e) {
			VirtualFile vfile = FileDocumentManager.getInstance().getFile(e.getDocument());
			if ( vfile!=null && vfile.getName().endsWith(".stg") ) {
				editorDocumentAlteredEvent(e.getDocument());
			}
		}
	}

	private class MyEditorFactoryListener extends EditorFactoryAdapter {
		@Override
		public void editorCreated(@NotNull EditorFactoryEvent event) {
			final Editor editor = event.getEditor();
			final Document doc = editor.getDocument();
			VirtualFile vfile = FileDocumentManager.getInstance().getFile(doc);
			if ( vfile!=null && vfile.getName().endsWith(".stg") ) {
				STGroupFileEditorListener listener = new STGroupFileEditorListener();
				doc.putUserData(EDITOR_DOCUMENT_LISTENER_KEY, listener);
				doc.addDocumentListener(listener);
			}
		}

		@Override
		public void editorReleased(@NotNull EditorFactoryEvent event) {
			Editor editor = event.getEditor();
			final Document doc = editor.getDocument();
			if (editor.getProject() != null && editor.getProject() != project) {
				return;
			}
			STGroupFileEditorListener listener = editor.getUserData(EDITOR_DOCUMENT_LISTENER_KEY);
			if (listener != null) {
				doc.removeDocumentListener(listener);
				doc.putUserData(EDITOR_DOCUMENT_LISTENER_KEY, null);
			}
			DocumentListener listener2 = editor.getUserData(EDITOR_STRUCTVIEW_LISTENER_KEY);
			if (listener2 != null) {
				doc.removeDocumentListener(listener2);
				doc.putUserData(EDITOR_STRUCTVIEW_LISTENER_KEY, null);
			}
		}
	}
}
