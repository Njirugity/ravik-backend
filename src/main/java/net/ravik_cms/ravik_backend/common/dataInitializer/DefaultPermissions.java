package net.ravik_cms.ravik_backend.common.dataInitializer;

import java.util.List;
import java.util.stream.Stream;

/**
 * Every permission the controllers check, in one place so the permissions seeder and the
 * default roles can't drift apart.
 */
public final class DefaultPermissions {

    public static final List<String> ALL = Stream.of(
            crud("PROJECT"),
            crud("USER"),
            crud("ROLE"),
            crud("PHASE"),
            crud("MILESTONE"),
            crud("SCHEDULE"),
            crud("DEPENDENCIES"),
            crud("ATTENDANCE"),
            List.of("CREATE_WAGE", "READ_WAGE", "DELETE_WAGE"),
            crud("LABOUR"),
            crud("EQUIPMENT"),
            crud("SUBCONTRACTOR"),
            crud("MATERIAL_LIST"),
            crud("MATERIAL_REQUIRED"),
            crud("MATERIAL_DELIVERED"),
            crud("MATERIAL_USED"),
            List.of("READ_MATERIALS_REPORT", "READ_MATERIAL_ALERTS"),
            crud("BUDGET"),
            crud("PAYMENT"),
            crud("PAYOUT"),
            crud("INCOME"),
            crud("EXPENSE"),
            crud("ACCOUNT"),
            crud("APPROVAL"),
            List.of("UPLOAD_FILE", "READ_FILE", "DELETE_FILE")
    ).flatMap(List::stream).toList();

    public static final List<String> ALL_READ = ALL.stream()
            .filter(p -> p.startsWith("READ_"))
            .toList();

    private DefaultPermissions() {
    }

    public static List<String> crud(String group) {
        return List.of("CREATE_" + group, "READ_" + group, "UPDATE_" + group, "DELETE_" + group);
    }
}
