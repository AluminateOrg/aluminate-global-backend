package com.aluminate.aluminate_global_backend.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

@Service
public class OrgContainerService {

    private static final String ORG_TEMPLATE_PATH = "/app/org-template"; // path inside the container

    public boolean createOrgContainer(String orgSlug) {
        try {
            // Set up environment variables
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "docker-compose",
                    "-f", ORG_TEMPLATE_PATH + "/docker-compose-org.yml",
                    "-p", orgSlug,
                    "up", "--build", "-d"
            );

            // Pass environment vars (ORG_SLUG)
            Map<String, String> env = processBuilder.environment();
            env.put("ORG_SLUG", orgSlug);
            // You can pass more env vars if needed (e.g. API keys)

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Capture output for debugging/logging
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

