package de.choffmeister.activitidemo;

import java.util.List;
import java.util.Map;

import org.activiti.engine.*;
import org.activiti.engine.runtime.*;
import org.activiti.engine.task.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     * 
     * @param testName
     *            name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testApp() {
        // Create Activiti process engine
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault()
                .buildProcessEngine();

        // Get Activiti services
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();

        // Deploy the process definition
        repositoryService.createDeployment().addClasspathResource("test1.bpmn20.xml").deploy();

        // Assert that no process instance is running
        assertEquals(0L, runtimeService.createProcessInstanceQuery().count());

        // Start a process instance
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("financialReport");

        // Assert that one process instance is running
        assertEquals(1L, runtimeService.createProcessInstanceQuery().count());

        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
        Task task = tasks.get(0);
        
        Map<String, Object> variables = taskService.getVariables(task.getId());

        taskService.claim(task.getId(), "fozzie");
        taskService.complete(task.getId());

        List<Task> tasks2 = taskService.createTaskQuery().taskCandidateGroup("management").list();
        Task task2 = tasks2.get(0);

        taskService.claim(task2.getId(), "fozzie");
        taskService.complete(task2.getId());

        // Assert that no process instance is running anymore
        assertEquals(0L, runtimeService.createProcessInstanceQuery().count());
    }
}
