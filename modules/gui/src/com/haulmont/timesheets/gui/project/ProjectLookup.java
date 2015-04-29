/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui.project
 */
package com.haulmont.timesheets.gui.project;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.service.ProjectsService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 */
public class ProjectLookup extends AbstractLookup {

    @Inject
    protected TreeTable projectsTable;
    @Inject
    protected HierarchicalDatasource<Project, UUID> projectsDs;
    @Inject
    protected ProjectsService projectsService;

    @Override
    public void init(Map<String, Object> params) {
        projectsTable.addAction(new CustomEditAction(projectsTable));

        Project project = (Project) params.get("parentProject");
        if (project != null) {
            projectsDs.excludeItem(project);
            List<Project> childrenProjects = projectsService.getChildren(project);
            for (Project child : childrenProjects) {
                projectsDs.excludeItem(child);
            }
        }
    }

    protected class CustomEditAction extends EditAction {

        public CustomEditAction(ListComponent target) {
            super(target);
        }

        @Override
        protected void afterCommit(Entity entity) {
            projectsTable.refresh();
        }
    }
}