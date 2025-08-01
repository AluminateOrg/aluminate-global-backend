package com.aluminate.aluminate_global_backend.controller.secure;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling secure admin endpoints.
 * All endpoints in this controller are prefixed with /admin/secure/
 * and are intended for administrative operations that require authentication & REQUIRES ACTIVE PACKAGE!.
 */
@RestController
@RequestMapping("${api.prefix}/admin/secure/")
public class SecureController {

}
