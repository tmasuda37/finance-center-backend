package com.tmasuda.fc.handler;

import com.tmasuda.fc.ctrl.AccountBalanceCtrl;
import com.tmasuda.fc.ctrl.AccountCtrl;
import com.tmasuda.fc.ctrl.CategoryCtrl;
import com.tmasuda.fc.ctrl.TransactionCtrl;
import com.tmasuda.fc.model.Account;
import com.tmasuda.fc.model.AccountBalance;
import com.tmasuda.fc.model.Transaction;
import com.tmasuda.fc.model.TransactionFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/transaction")
@Controller
public class TransactionHandler {

    private static final Logger _logger = Logger.getLogger(TransactionHandler.class);

    @Autowired
    private AccountCtrl accountCtrl;

    @Autowired
    private AccountBalanceCtrl accountBalanceCtrl;

    @Autowired
    private CategoryCtrl categoryCtrl;

    @Autowired
    private TransactionCtrl transactionCtrl;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public AccountBalance create(@RequestAttribute(value = "SNS_ID") String snsId, @RequestBody @Valid Transaction aTransaction) throws Exception {
        aTransaction.account = accountCtrl.findAccountBySnsId(snsId);

        if (aTransaction.account == null) {
            throw new Exception("Account Error!");
        }

        aTransaction.category = categoryCtrl.findCategoryByPublicIdAndHouseHold(aTransaction.category.publicId, aTransaction.account.houseHold);

        if (aTransaction.category == null) {
            throw new Exception("Category Error!");
        }

        aTransaction = transactionCtrl.createTransaction(aTransaction);

        return accountBalanceCtrl.getBalance(aTransaction);
    }

    @RequestMapping(value = "/createAll", method = RequestMethod.POST)
    @ResponseBody
    public void createAll(@RequestAttribute(value = "SNS_ID") String snsId, @RequestBody @Valid List<Transaction> transactionList) throws Exception {
        for (Transaction aTransaction : transactionList) {
            aTransaction.account = accountCtrl.findAccountBySnsId(snsId);

            if (aTransaction.account == null) {
                throw new Exception("Account Error!");
            }

            aTransaction.category = categoryCtrl.findCategoryByPublicIdAndHouseHold(aTransaction.category.publicId, aTransaction.account.houseHold);

            if (aTransaction.category == null) {
                throw new Exception("Category Error!");
            }

            if (!transactionCtrl.hasSameTransaction(aTransaction)) {
                transactionCtrl.createTransaction(aTransaction);
            } else {
                _logger.warn("This is skipped as the transaction may be duplicated." + aTransaction.toString());
            }
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Page<Transaction> list(@RequestAttribute(value = "SNS_ID") String snsId, @RequestBody TransactionFilter aTransactionFilter) throws Exception {
        Account anAccount = accountCtrl.findAccountBySnsId(snsId);

        if (anAccount == null) {
            throw new Exception("Account Error!");
        }

        _logger.debug("Filter parameters: " + aTransactionFilter.toString());

        PageRequest aPageRequest = new PageRequest(aTransactionFilter.page, aTransactionFilter.size);

        return transactionCtrl.list(aPageRequest, anAccount);
    }

}
