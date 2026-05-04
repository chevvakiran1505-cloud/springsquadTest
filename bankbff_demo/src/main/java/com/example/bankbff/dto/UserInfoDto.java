package com.example.bankbff.dto;

import java.util.List;

/**
 * Information about the authenticated user, exposed via /api/me.
 *
 * The SPA calls /api/me on load to determine whether the user is logged in
 * and to display their name in the header. If the user is not authenticated,
 * the BFF returns 401, signalling the SPA to show the login button instead.
 */


public record UserInfoDto(
        String userId,
        String email,
        String displayName,
        List<String> role
) {
}