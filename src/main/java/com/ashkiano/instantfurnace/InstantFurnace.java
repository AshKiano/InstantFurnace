package com.ashkiano.instantfurnace;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InstantFurnace extends JavaPlugin implements Listener {

    private final Map<Material, ItemStack> smeltingMap = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        initializeSmeltingMap();
        getLogger().info("InstantFurnace enabled!");
        Metrics metrics = new Metrics(this, 22053);
        this.getLogger().info("Thank you for using the InstantFurnace plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");
    }

    @Override
    public void onDisable() {
        getLogger().info("InstantFurnace disabled!");
    }

    private void initializeSmeltingMap() {
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            if (recipe instanceof FurnaceRecipe) {
                FurnaceRecipe furnaceRecipe = (FurnaceRecipe) recipe;
                smeltingMap.put(furnaceRecipe.getInput().getType(), furnaceRecipe.getResult());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof FurnaceInventory) {
            FurnaceInventory furnaceInventory = (FurnaceInventory) event.getClickedInventory();
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();

                if (player.hasPermission("instantfurnace.use")) {
                    ItemStack source = furnaceInventory.getSmelting();

                    if (source != null && smeltingMap.containsKey(source.getType())) {
                        ItemStack result = smeltingMap.get(source.getType());
                        furnaceInventory.setSmelting(new ItemStack(Material.AIR));
                        ItemStack currentResult = furnaceInventory.getResult();
                        if (currentResult != null && currentResult.getType() == result.getType()) {
                            currentResult.setAmount(currentResult.getAmount() + source.getAmount());
                            furnaceInventory.setResult(currentResult);
                        } else {
                            furnaceInventory.setResult(new ItemStack(result.getType(), source.getAmount()));
                        }
                        player.sendMessage("§aYour items have been instantly smelted!");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}