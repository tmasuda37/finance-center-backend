package com.tmasuda.fc.handler;

import com.tmasuda.fc.ctrl.AccountCtrl;
import com.tmasuda.fc.ctrl.CategoryCtrl;
import com.tmasuda.fc.model.Account;
import com.tmasuda.fc.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/category")
@Controller
public class CategoryHandler {

    @Autowired
    private AccountCtrl accountCtrl;

    @Autowired
    private CategoryCtrl categoryCtrl;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<Category> list(@RequestAttribute(value = "SNS_ID") String snsId) throws Exception {
        Account anAccount = accountCtrl.createAccount(snsId);

        if (anAccount == null) {
            throw new Exception("Account Error!");
        }

        return categoryCtrl.findCategoriesByHouseHold(anAccount.houseHold);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public List<Category> create(@RequestAttribute(value = "SNS_ID") String snsId, @RequestBody @Valid Category aCategory) throws Exception {
        Account anAccount = accountCtrl.createAccount(snsId);

        if (anAccount == null) {
            throw new Exception("Account Error!");
        }

        categoryCtrl.createCategory(anAccount.houseHold, aCategory.name, aCategory.toExpense);

        return categoryCtrl.findCategoriesByHouseHold(anAccount.houseHold);
    }

}
