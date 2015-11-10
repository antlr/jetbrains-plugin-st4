package org.antlr.jetbrains.st4plugin;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
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
import org.jetbrains.annotations.NotNull;

public class STGroupPluginController implements ProjectComponent {
	public static final String PLUGIN_ID = "org.antlr.jetbrains.st4plugin";
	public static final Logger LOG = Logger.getInstance("STGroupPluginController");
	public static final Key<STGroupFileEditorListener> EDITOR_DOCUMENT_LISTENER_KEY =
		Key.create("EDITOR_DOCUMENT_LISTENER_KEY");

	public Project project;
	public boolean projectIsClosed = false;

	public MyVirtualFileAdapter myVirtualFileAdapter = new MyVirtualFileAdapter();
	public MyFileEditorManagerAdapter myFileEditorManagerAdapter = new MyFileEditorManagerAdapter();

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
	public void projectClosed() {
		LOG.info("projectClosed " + project.getName());
		//synchronized ( shutdownLock ) { // They should be called from EDT only so no lock
		projectIsClosed = true;
		project = null;
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
	public void initComponent() { }

	@Override
	public void disposeComponent() { }

	@NotNull
	@Override
	public String getComponentName() {
		return "st.ProjectComponent";
	}

	public void installListeners() {
		LOG.info("installListeners "+project.getName());
		// Listen for .stg file saves
		VirtualFileManager.getInstance().addVirtualFileListener(myVirtualFileAdapter);

		// Listen for editor window changes
		MessageBusConnection msgBus = project.getMessageBus().connect(project);
		msgBus.subscribe(
			FileEditorManagerListener.FILE_EDITOR_MANAGER,
			myFileEditorManagerAdapter
		);

		// Listen for editor creation and release so that we can install
		// keyboard listeners that notify us when to reanalyze the file.
		EditorFactory factory = EditorFactory.getInstance();
		factory.addEditorFactoryListener(
			new EditorFactoryAdapter() {
				@Override
				public void editorCreated(@NotNull EditorFactoryEvent event) {
					final Editor editor = event.getEditor();
					final Document doc = editor.getDocument();
					VirtualFile vfile = FileDocumentManager.getInstance().getFile(doc);
					if ( vfile!=null && vfile.getName().endsWith(".stg") ) {
						STGroupFileEditorListener listener = new STGroupFileEditorListener(project);
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
				}
			}
		);
	}

	public void fileSavedEvent(VirtualFile file) {
		LOG.info("fileSavedEvent "+(file!=null?file.getPath():"none")+" "+project.getName());
	}

	public void currentEditorFileChangedEvent(VirtualFile oldFile, VirtualFile newFile) {
		LOG.info("currentEditorFileChangedEvent "+(oldFile!=null?oldFile.getPath():"none")+
				 " -> "+(newFile!=null?newFile.getPath():"none")+" "+project.getName());
		if ( newFile==null ) { // all files must be closed I guess
			return;
		}
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

	private class MyVirtualFileAdapter extends VirtualFileAdapter {
		@Override
		public void contentsChanged(VirtualFileEvent event) {
			final VirtualFile vfile = event.getFile();
			if ( !vfile.getName().endsWith(".stg") ) return;
			if ( !projectIsClosed ) fileSavedEvent(vfile);
		}
	}

	private class MyFileEditorManagerAdapter extends FileEditorManagerAdapter {
		@Override
		public void selectionChanged(FileEditorManagerEvent event) {
			if ( !projectIsClosed ) currentEditorFileChangedEvent(event.getOldFile(), event.getNewFile());
		}

		@Override
		public void fileClosed(FileEditorManager source, VirtualFile file) {
			if ( !projectIsClosed ) editorFileClosedEvent(file);
		}
	}
}
