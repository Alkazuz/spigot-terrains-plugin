package br.alkazuz.terrenos.economy;

import br.alkazuz.terrenos.Main;

public class DepositBank {
    private static final String BANK = "nightcraft";

    public static void depositBank(double value) {
        Main.getInstance().getEconomy().depositPlayer(BANK, value);
    }
}
