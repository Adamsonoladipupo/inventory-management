package com.inventory_management;

import org.junit.jupiter.api.Test;

class InventoryManagementApplicationTests {

    @Test
    void applicationClassExists() {
        // Verifies the application class compiles and is loadable.
        // Full context tests require a running PostgreSQL instance.
        new InventoryManagementApplication();
    }
}
