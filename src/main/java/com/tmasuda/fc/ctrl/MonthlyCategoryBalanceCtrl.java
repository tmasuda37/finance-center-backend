package com.tmasuda.fc.ctrl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.tmasuda.fc.model.MonthlyCategoryBalance;
import com.tmasuda.fc.model.Transaction;
import com.tmasuda.fc.model.key.MonthlyCategoryBalanceKey;
import com.tmasuda.fc.repo.MonthlyCategoryBalanceRepo;

@Controller
public class MonthlyCategoryBalanceCtrl extends AbstractCtrl<MonthlyCategoryBalance> {

	@Autowired
	private MonthlyCategoryBalanceRepo categoryBalanceRepo;

	@Override
	public MonthlyCategoryBalance getSavedModel(MonthlyCategoryBalance instantiated) {
		return categoryBalanceRepo.findOne(instantiated.aMonthlyCategoryBalanceKey);
	}

	@Override
	public void preRun(MonthlyCategoryBalance instantiated) {
	}

	@Override
	public MonthlyCategoryBalance createNewModel(MonthlyCategoryBalance instantiated) {
		return categoryBalanceRepo.save(instantiated);
	}

	@Override
	public void postRun(MonthlyCategoryBalance committed) {
	}

	public void updateBalance(Transaction aTransaction) {
		MonthlyCategoryBalance instantiated = getBalance(getBalanceKey(aTransaction));
		instantiated.amount = calcBalance(instantiated, aTransaction);
		categoryBalanceRepo.save(instantiated);
	}

	protected BigDecimal calcBalance(MonthlyCategoryBalance aBalance, Transaction aTransaction) {
		BigDecimal result;

		if (aTransaction.category.toExpense) {
			result = aBalance.amount.subtract(aTransaction.amount);
		} else {
			result = aBalance.amount.add(aTransaction.amount);
		}

		return result;
	}

	private MonthlyCategoryBalance getBalance(MonthlyCategoryBalanceKey aMonthlyCategoryBalanceKey) {
		return getOrCreateModel(new MonthlyCategoryBalance(aMonthlyCategoryBalanceKey));
	}

	private MonthlyCategoryBalanceKey getBalanceKey(Transaction aTransaction) {
		return new MonthlyCategoryBalanceKey(aTransaction.account, aTransaction.currency, aTransaction.category, aTransaction.calendar);
	}

}