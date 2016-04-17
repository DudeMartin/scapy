package org.scapy.core.mod;

import org.scapy.Application;
import org.scapy.Settings;
import org.scapy.Settings.DefaultSettings;
import org.scapy.utils.WebUtilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

final class Hooks {

    private static final String DEFAULT_REPOSITORY = "https://www.dropbox.com/s/ulyzhblbdjy6w72/hooks.txt?dl=1";
    private static Path hookPath;

    static Scanner getData(int targetRevision) throws IOException {
        String data;
        Scanner scanner;
        if (Application.isVirtualMode()) {
            data = downloadData(false);
        } else {
            hookPath = Application.getApplicationPath("data", "hooks.txt");
            if (Files.exists(hookPath)) {
                data = new String(Files.readAllBytes(hookPath));
                scanner = new Scanner(data);
                if (scanner.nextInt() != targetRevision) {
                    data = downloadData(true);
                }
            } else {
                data = downloadData(true);
            }
        }
        scanner = new Scanner(data);
        int currentRevision = scanner.nextInt();
        if (currentRevision != targetRevision) {
            throw new HookDataException("The revision of the hook data is outdated (current: " + currentRevision + " target: " + targetRevision + ").");
        }
        return scanner;
    }

    private static String downloadData(boolean store) throws IOException {
        String repositoryAddress = Settings.get(DefaultSettings.HOOK_REPOSITORY, DEFAULT_REPOSITORY);
        byte[] rawData = WebUtilities.download(repositoryAddress);
        if (store) {
            Files.write(hookPath, rawData);
        }
        return new String(rawData);
    }
}