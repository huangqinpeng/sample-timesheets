/*
 * Copyright (c) 2015 com.haulmont.ts.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.timesheets.entity.Client;
import com.haulmont.timesheets.entity.Project;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gorelov
 */
@Service(ProjectsService.NAME)
public class ProjectsServiceBean implements ProjectsService {

    @Inject
    protected Persistence persistence;

    protected List<Project> getAllProjects() {
        EntityManager entityManager = persistence.getEntityManager();
        TypedQuery<Project> query = entityManager.createQuery("select e from ts$Project e", Project.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public List<Project> getChildren(Project parent) {
        List<Project> projects = getAllProjects();
        if (!projects.isEmpty()) {
            List<Project> children = new ArrayList<>();
            for (Project project : projects) {
                if (parent.equals(project.getParent())) {
                    children.add(project);
                    children.addAll(getChildren(project));
                }
            }
            return children;
        }
        return Collections.emptyList();
    }

    @Override
    public void setClient(Project project, Client client) {
        Transaction tx = persistence.createTransaction();
        try {
            List<Project> projects = getAllProjects();
            for (Project entity : projects) {
                if (entity.equals(project)) {
                    entity.setClient(client);
                }
            }
            tx.commit();
        } finally {
            tx.end();
        }
    }
}