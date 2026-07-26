package org.example.i2iacademyohmguard2.billing_accounts;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.billing_accounts.dto.BillingAccountInfos;
import org.example.i2iacademyohmguard2.users.UsersService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/billing-accounts")
@RequiredArgsConstructor
public class BillingAccountsController {
    private final BillingAccountsService billingAccountsService;
    private final UsersService usersService;

    /*her ay ölçümleri güncelle*/
    @Scheduled(cron = "0 0 0 1 * ?")
    public void cleanBillingAccounts(){
        billingAccountsService.cleanBillingAccounts();
    }

    @Scheduled(fixedRate = 300000)
    public void syncBillingAccounts(){
        billingAccountsService.syncBillingAccounts();
    }



    @GetMapping("/info")
    public ResponseEntity<BillingAccountInfos> getBillingAccountsInfo(@RequestHeader("Authorization") String token, @RequestParam @NotNull UUID homeId){
        UsersTable findUserIdByToken = usersService.findByToken(token);
        BillingAccountInfos billingAccountInfos = billingAccountsService.getBillingAccountInfos(findUserIdByToken,homeId);
        return ResponseEntity.ok(billingAccountInfos);
    }


}
