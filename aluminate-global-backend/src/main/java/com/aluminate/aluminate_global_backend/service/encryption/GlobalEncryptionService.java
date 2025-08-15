package com.aluminate.aluminate_global_backend.service.encryption;

import com.aluminate.aluminate_global_backend.config.util.RSAEncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class GlobalEncryptionService {
    private final PrivateKey globalPrivateKey;
    private final PublicKey orgPublicKey;

    @Autowired
    public GlobalEncryptionService(
            @Value("${encryption.global.private-key}") String globalPrivateKeyPem,
            @Value("${encryption.organization.public-key}") String orgPublicKeyPem) throws Exception {

        this.globalPrivateKey = RSAEncryptionUtil.privateKeyFromPem(globalPrivateKeyPem);
        this.orgPublicKey = RSAEncryptionUtil.publicKeyFromPem(orgPublicKeyPem);
    }

    public String encryptForOrganization(String plaintext) throws Exception {
        return RSAEncryptionUtil.encrypt(plaintext, orgPublicKey);
    }

    public String decryptFromOrganization(String ciphertext) throws Exception {
        return RSAEncryptionUtil.decrypt(ciphertext, globalPrivateKey);
    }
}
