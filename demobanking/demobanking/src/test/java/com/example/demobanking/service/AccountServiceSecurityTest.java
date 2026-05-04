package com.example.demobanking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AccountServiceSecurityTest {

    @Autowired
    private AccountService accountService;

    // @WithMockUser populates the SecurityContext with a mock Authentication.
    // No Authorization Server or real token is needed.
    // "SCOPE_read:accounts" is the authority Spring Security derives from the
    // "read:accounts" scope claim when processing a real JWT. The SCOPE_ prefix
    // is added automatically by Spring Security's JWT converter.
    @Test
    @WithMockUser(authorities = {"SCOPE_read:accounts"})
    void findAll_withReadScope_returnsAccounts() {
        var result = accountService.findAll();
        assertThat(result).isNotEmpty();
    }

    @Test
    @WithMockUser   // authenticated but no read:accounts scope
    void findAll_withoutReadScope_throwsAccessDeniedException() {
        // TODO 29: Assert that calling accountService.findAll() throws AccessDeniedException.

        assertThatThrownBy(() -> accountService.findAll())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void findAll_withNoAuthentication_throwsAccessDeniedException() {
        // TODO 30: Call accountService.findAll() with no @WithMockUser annotation
        // (unauthenticated SecurityContext) and assert that AccessDeniedException is thrown.
        // This verifies the method is not accidentally callable without any authentication.

            // No @WithMockUser -- the SecurityContext is empty (unauthenticated).
            // Spring Security evaluates hasAuthority(...) against an empty context
            // and throws AccessDeniedException rather than returning null or an empty list.
            assertThatThrownBy(() -> accountService.findAll())
                    .isInstanceOf(AccessDeniedException.class);

    }

    // TODO 31: Write a test verifying that create() succeeds when the caller holds
    // both SCOPE_write:accounts and SCOPE_read:accounts.
    // @WithMockUser(authorities = {"SCOPE_write:accounts", "SCOPE_read:accounts"})

    // TODO 32: Write a test verifying that create() throws AccessDeniedException
    // when the caller holds SCOPE_write:accounts but NOT SCOPE_read:accounts.
    // This validates the compound @PreAuthorize expression on the create() method.
    @Test
    @WithMockUser(authorities = {"SCOPE_write:accounts"})
    void create_withWriteScopeButNoReadScope_throwsAccessDeniedException() {
        // TODO: Assert that create() throws AccessDeniedException.
    }
}
