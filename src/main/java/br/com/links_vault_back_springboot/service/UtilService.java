package br.com.links_vault_back_springboot.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilService {

    public UUID extractUserIdFromRequest(
            HttpServletRequest httpServletRequest
    ) {
        var userId = httpServletRequest.getAttribute("user_id");
        return UUID.fromString(userId.toString());
    }

}
