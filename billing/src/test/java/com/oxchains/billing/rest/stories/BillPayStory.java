package com.oxchains.billing.rest.stories;

import com.oxchains.billing.rest.steps.BillSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

/**
 * @author aiet
 */
public class BillPayStory {

  @Steps
  BillSteps steps;

  @Given("bill in $action list of $user")
  public void givenBillInAcceptanceList(String action, String user) {
    steps.billNotEmpty(action, user);
  }

  @When("I accept it as $user")
  public void whenIAcceptIt(String user) {
    steps.acceptBill(user);
  }

  @When("I present $action of the bill to $user as $by")
  public void whenIPresentActionTo(String action, String user, String by) throws Exception {
    steps.present(user, action, by);
  }

  @When("I have the bill pledged for $user as $as")
  public void whenIHaveTheBillPledgedForUser(String user, String as){
    steps.pledgeBill(user, as);
  }

  @When("I have the bill pledge released by $user as $as")
  public void whenIHaveTheBillPledgeReleasedForUser(String user, String as){
    steps.releasePledge(user, as);
  }

  @Then("the bill is in the $action list of $user")
  public void thenTheBillIsInTheListOf(String action, String user) {
    steps.billInListOf(action, user);
  }

  @When("I have the bill discounted for $user as $as")
  public void whenIHaveTheBillDiscountForUser(String user, String as){
    steps.discountBill(user, as);
  }

  @Then("the new bill is accepted by $user")
  public void thenTheNewBillIsAccepted(String user) {
    steps.billAccepted(user);
  }

  @When("I have the bill guaranteed by $user as $as")
  public void whenIHaveTheBillGuaranteedByUser(String user, String as) {
    steps.guaranteeBill(user, as);
  }

  @Then("the bill is guaranteed by $user")
  public void thenTheBillIsGuaranteed(String user) {
    steps.billGuaranteed(user);
  }

  @When("I have the bill received by $user as $as")
  public void whenIHaveTheBillReceivedByUser(String user, String as) {
    steps.receiveBill(user, as);
  }

  @When("I have the bill revoked as $user")
  public void whenIHaveTheBillRevokedByUser(String user){
    steps.rejectBillPayment(user);
  }

  @Then("the bill is received by $user")
  public void thenTheBillIsReceivedBy(String user) {
    steps.billReceived(user);
  }

  @When("I have the bill paid by $user as $user")
  public void whenIHaveTheBillPaidByUser(String user, String as) {
    steps.payBill(user, as);
  }

  @Then("the bill is paid by $user")
  public void thenTheBillIsPaidBy(String user){
    steps.billPaid(user);
  }

  @When("I have the bill endorsed by $user as $as")
  public void whenIHaveTheBillEndorsedByUser(String user, String as){
    steps.billEndorsed(user, as);
  }
}
