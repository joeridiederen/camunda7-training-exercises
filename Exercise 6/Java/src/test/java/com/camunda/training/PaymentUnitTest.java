package com.camunda.training;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = { "PaymentProcess.bpmn" })
public class PaymentUnitTest {
  
	@Test
    public void happy_path_test() {
		ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("PaymentProcess", withVariables("orderTotal", 45.99, "customerCredit", 30.00));
	    
		assertThat(processInstance).isWaitingAt("DeductCreditTask").externalTask().hasTopicName("creditDeduction");    
		
		complete(externalTask());
		
	    assertThat(processInstance).isWaitingAt("ChargeCreditCardTask").externalTask().hasTopicName("creditCardCharging");
	    
	    complete(externalTask());
	    
	    assertThat(processInstance).isEnded().hasPassed("PaymentCompletedEvent");
    }

}
