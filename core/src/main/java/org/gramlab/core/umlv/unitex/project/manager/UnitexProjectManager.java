package org.gramlab.core.umlv.unitex.project.manager;

import java.io.File;

import org.gramlab.core.umlv.unitex.common.project.Project;
import org.gramlab.core.umlv.unitex.common.project.manager.ProjectManager;
import org.gramlab.core.umlv.unitex.project.UnitexProject;

/**
 * 
 * @author mdamis
 *
 */
public class UnitexProjectManager {
	private static UnitexProject project;
	
	public UnitexProjectManager(UnitexProject project) {
		this.project = project;
	}

	public static Project search(File resource) {
		return project;
	}

	public static Project search(File resource, boolean weShallOpenTheProject) {
		return project;
	}
}
