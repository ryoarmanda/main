package seedu.moolah.logic.commands.budget;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.moolah.logic.commands.CommandTestUtil.DESC_CHICKEN;
import static seedu.moolah.logic.commands.CommandTestUtil.VALID_EXPENSE_CATEGORY_CHICKEN;
import static seedu.moolah.logic.commands.CommandTestUtil.VALID_EXPENSE_DESCRIPTION_TAXI;
import static seedu.moolah.logic.commands.CommandTestUtil.VALID_EXPENSE_PRICE_TAXI;
import static seedu.moolah.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.moolah.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.moolah.testutil.TestUtil.makeModelStack;
import static seedu.moolah.testutil.TypicalIndexes.INDEX_FIRST;
import static seedu.moolah.testutil.TypicalIndexes.INDEX_SECOND;
import static seedu.moolah.testutil.TypicalMooLah.getTypicalMooLah;

import org.junit.jupiter.api.Test;

import seedu.moolah.commons.core.Messages;
import seedu.moolah.commons.core.index.Index;
import seedu.moolah.logic.commands.expense.EditExpenseCommand.EditExpenseDescriptor;
import seedu.moolah.logic.commands.general.ClearCommand;
import seedu.moolah.model.Model;
import seedu.moolah.model.ModelHistory;
import seedu.moolah.model.ModelManager;
import seedu.moolah.model.MooLah;
import seedu.moolah.model.UserPrefs;
import seedu.moolah.model.budget.Budget;
import seedu.moolah.model.expense.Expense;
import seedu.moolah.testutil.EditExpenseDescriptorBuilder;
import seedu.moolah.testutil.ExpenseBuilder;

public class EditExpenseFromBudgetCommandTest {
    private Model model = new ModelManager(getTypicalMooLah(), new UserPrefs(), new ModelHistory());

    /*
    @Test
    public void run_allFieldsSpecified_success() {
        Expense editedExpense = new ExpenseBuilder()
                .withDescription(VALID_EXPENSE_DESCRIPTION_TAXI)
                .withPrice(VALID_EXPENSE_PRICE_TAXI)
                .withCategory(VALID_EXPENSE_CATEGORY_TAXI)
                .withTimestamp(VALID_EXPENSE_TIMESTAMP_TAXI)
                .withUniqueIdentifier(CHICKEN_RICE.getUniqueIdentifier().value)
                .build();
        EditExpenseDescriptor descriptor = new EditExpenseDescriptorBuilder(editedExpense).build();
        EditExpenseFromBudgetCommand editExpenseFromBudgetCommand = new EditExpenseFromBudgetCommand(
                INDEX_FIRST, descriptor);

        assertTrue(model.getPrimaryBudget().getCurrentPeriodExpenses().get(INDEX_FIRST.getZeroBased())
                .equals(CHICKEN_RICE));
        String expectedMessage = String.format(EditExpenseFromBudgetCommand.MESSAGE_EDIT_EXPENSE_SUCCESS,
                editedExpense);

        Model expectedModel = new ModelManager(new MooLah(model.getMooLah()),
                new UserPrefs(), new ModelHistory());
        Expense expenseToEdit = model.getPrimaryBudget().getCurrentPeriodExpenses().get(INDEX_FIRST.getZeroBased());
        expectedModel.setExpense(expenseToEdit, editedExpense);
        Budget primaryBudget = expectedModel.getPrimaryBudget();
        Budget primaryBudgetCopy = primaryBudget.deepCopy();
        primaryBudgetCopy.setExpense(expenseToEdit, editedExpense);
        expectedModel.setBudget(primaryBudget, primaryBudgetCopy);
        expectedModel.setModelHistory(new ModelHistory("", makeModelStack(model), makeModelStack()));

        assertCommandSuccess(editExpenseFromBudgetCommand, model, expectedMessage, expectedModel);
    }

     */

    @Test
    public void run_someFieldsSpecified_success() {
        Index indexLastExpense = Index.fromOneBased(model.getPrimaryBudget().getCurrentPeriodExpenses().size());
        Expense lastExpense = model.getPrimaryBudget().getCurrentPeriodExpenses().get(indexLastExpense.getZeroBased());

        ExpenseBuilder expenseInList = new ExpenseBuilder(lastExpense);
        Expense editedExpense = expenseInList
                .withDescription(VALID_EXPENSE_DESCRIPTION_TAXI)
                .withPrice(VALID_EXPENSE_PRICE_TAXI)
                .withCategory(VALID_EXPENSE_CATEGORY_CHICKEN)
                .build();

        EditExpenseDescriptor descriptor = new EditExpenseDescriptorBuilder()
                .withDescription(VALID_EXPENSE_DESCRIPTION_TAXI)
                .withPrice(VALID_EXPENSE_PRICE_TAXI)
                .withCategory(VALID_EXPENSE_CATEGORY_CHICKEN)
                .build();
        EditExpenseFromBudgetCommand editExpenseFromBudgetCommand = new EditExpenseFromBudgetCommand(
                indexLastExpense, descriptor);

        String expectedMessage = String.format(EditExpenseFromBudgetCommand.MESSAGE_EDIT_EXPENSE_SUCCESS,
                editedExpense);

        Model expectedModel = new ModelManager(new MooLah(model.getMooLah()),
                new UserPrefs(), new ModelHistory());

        expectedModel.setExpense(lastExpense, editedExpense);
        Budget primaryBudget = expectedModel.getPrimaryBudget();
        Budget primaryBudgetCopy = primaryBudget.deepCopy();
        primaryBudgetCopy.setExpense(lastExpense, editedExpense);
        expectedModel.setBudget(primaryBudget, primaryBudgetCopy);

        expectedModel.setModelHistory(new ModelHistory("", makeModelStack(model), makeModelStack()));

        assertCommandSuccess(editExpenseFromBudgetCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void run_noFieldSpecified_success() {
        EditExpenseFromBudgetCommand editExpenseFromBudgetCommand =
                new EditExpenseFromBudgetCommand(INDEX_FIRST, new EditExpenseDescriptor());
        Expense editedExpense = model.getPrimaryBudget().getCurrentPeriodExpenses().get(INDEX_FIRST.getZeroBased());

        String expectedMessage = String.format(EditExpenseFromBudgetCommand.MESSAGE_EDIT_EXPENSE_SUCCESS,
                editedExpense);

        Model expectedModel = new ModelManager(new MooLah(model.getMooLah()),
                new UserPrefs(), new ModelHistory());
        expectedModel.commitModel("");

        assertCommandSuccess(editExpenseFromBudgetCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void run_invalidExpenseIndex_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getPrimaryBudget().getCurrentPeriodExpenses().size() + 1);
        EditExpenseDescriptor descriptor = new EditExpenseDescriptorBuilder()
                .withDescription(VALID_EXPENSE_DESCRIPTION_TAXI).build();
        EditExpenseFromBudgetCommand editExpenseFromBudgetCommand = new EditExpenseFromBudgetCommand(
                outOfBoundIndex, descriptor);

        assertCommandFailure(editExpenseFromBudgetCommand, model, Messages.MESSAGE_INVALID_EXPENSE_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditExpenseFromBudgetCommand standardCommand = new EditExpenseFromBudgetCommand(
                INDEX_FIRST, DESC_CHICKEN);

        // same values -> returns true
        EditExpenseDescriptor copyDescriptor = new EditExpenseDescriptor(DESC_CHICKEN);
        EditExpenseFromBudgetCommand commandWithSameValues = new EditExpenseFromBudgetCommand(
                INDEX_FIRST, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditExpenseFromBudgetCommand(INDEX_SECOND, DESC_CHICKEN)));
    }
}
