package me.harry.baedal.presentation.controller;

import me.harry.baedal.domain.model.user.UserRole;
import me.harry.baedal.presentation.annotation.RoleOnly;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/menus")
public class MenuController {
    @GetMapping
    @RoleOnly(roles = {UserRole.ROLE_SHOP_OWNER, UserRole.ROLE_ADMIN})
    public String example() {
        System.out.println("!!!!!!!!!!!!!!");
        System.out.println("Hello World!!!");
        return "Hello World";
    }
}
