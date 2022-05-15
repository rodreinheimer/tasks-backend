package br.ce.wcaquino.taskbackend.controller;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import br.ce.wcaquino.taskbackend.utils.ValidationException;
import junit.framework.Assert;

public class TaskControllerTest {
	
	@Mock
	private TaskRepo taskRepo;
	
	@InjectMocks
	private TaskController controller;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldNotSaveTaskWithoutDescription() {
		
		Task todo = new Task();
		todo.setDueDate(LocalDate.now());
		try {
			controller.save(todo);
			Assert.fail("Should not reach this point!");
		} catch (ValidationException e) {
			Assert.assertEquals("Fill the task description", e.getMessage());
		}
	}
	
	@Test
	public void shouldNotSaveTaskWithoutDate() {
		
		Task todo = new Task();
		todo.setTask("Description");
		try {
			controller.save(todo);
			Assert.fail("Should not reach this point!");
		} catch (ValidationException e) {
			Assert.assertEquals("Fill the due date", e.getMessage());
		}
		
	}
	
	@Test
	public void shouldNotSaveTaskWithPastDate() {
		Task todo = new Task();
		todo.setTask("Description");
		todo.setDueDate(LocalDate.of(2010, 01, 01));
		try {
			controller.save(todo);
			Assert.fail("Should not reach this point!");
		} catch (ValidationException e) {
			Assert.assertEquals("Due date must not be in past", e.getMessage());
		}
	}
	
	@Test
	public void shouldSaveTaskWithSuccess() throws ValidationException {
		Task todo = new Task();
		todo.setTask("Description");
		todo.setDueDate(LocalDate.now());
		controller.save(todo);
		Mockito.verify(taskRepo).save(todo);
	}

}
