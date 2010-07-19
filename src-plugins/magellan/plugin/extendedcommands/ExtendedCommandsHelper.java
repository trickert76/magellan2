// class magellan.plugin.extendedcommands.ExtendedCommandsHelper
// created on 02.02.2008
//
// Copyright 2003-2008 by magellan project team
//
// Author : $Author: $
// $Id: $
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program (see doc/LICENCE.txt); if not, write to the
// Free Software Foundation, Inc.,
// 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
package magellan.plugin.extendedcommands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import magellan.client.Client;
import magellan.client.event.UnitOrdersEvent;
import magellan.client.extern.MagellanPlugIn;
import magellan.library.Building;
import magellan.library.Faction;
import magellan.library.GameData;
import magellan.library.HasRegion;
import magellan.library.IntegerID;
import magellan.library.Item;
import magellan.library.LuxuryPrice;
import magellan.library.Region;
import magellan.library.RegionResource;
import magellan.library.Ship;
import magellan.library.Skill;
import magellan.library.StringID;
import magellan.library.Unit;
import magellan.library.UnitContainer;
import magellan.library.UnitID;
import magellan.library.gamebinding.EresseaConstants;
import magellan.library.gamebinding.GameSpecificRules;
import magellan.library.gamebinding.GameSpecificStuff;
import magellan.library.gamebinding.MovementEvaluator;
import magellan.library.rules.BuildingType;
import magellan.library.rules.ItemCategory;
import magellan.library.rules.ItemType;
import magellan.library.rules.Race;
import magellan.library.rules.RegionType;
import magellan.library.rules.SkillType;
import magellan.library.utils.Resources;
import magellan.library.utils.ShipRoutePlanner;
import magellan.library.utils.UnitRoutePlanner;
import magellan.library.utils.logging.Logger;

/**
 * A Helper class to have some shortcuts.
 * 
 * @author Thoralf Rickert
 * @version 1.0, 11.09.2007
 */
public class ExtendedCommandsHelper {

  private static final Logger log = Logger.getInstance(ExtendedCommandsHelper.class);

  private Client client;
  private GameData world;
  private Unit unit;
  private UnitContainer container;

  protected ExtendedCommandsHelper(Client client, GameData world) {
    this(client, world, null, null);
  }

  protected ExtendedCommandsHelper(Client client, GameData world, Unit unit) {
    this(client, world, unit, null);
  }

  protected ExtendedCommandsHelper(Client client, GameData world, UnitContainer container) {
    this(client, world, null, container);
  }

  protected ExtendedCommandsHelper(Client client, GameData world, Unit unit, UnitContainer container) {
    this.client = client;
    this.world = world;
    this.unit = unit;
    this.container = container;
  }

  protected GameData getData() {
    return world;
  }

  protected GameSpecificStuff getGamesSpecificStuff() {
    return world.rules.getGameSpecificStuff();
  }

  /**
   * The SkillType with the given name.
   * 
   * @param typeName
   * @return The SkillType or <code>null</code> if no such skill is known.
   */
  public SkillType getSkillType(String typeName) {
    return world.rules.getSkillType(StringID.create(typeName));
  }

  /**
   * The ItemType with the given name.
   * 
   * @param typeName
   * @return The ItemType or <code>null</code> if no such Item is known.
   */
  public ItemType getItemType(String typeName) {
    return world.rules.getItemType(StringID.create(typeName));
  }

  /**
   * The BuildingType with the given name.
   * 
   * @param typeName
   * @return The BuildingType or <code>null</code> if no such Building is known.
   */
  public BuildingType getBuildingType(String typeName) {
    return world.rules.getBuildingType(StringID.create(typeName));
  }

  /**
   * The Race with the given name.
   * 
   * @param typeName
   * @return The Race or <code>null</code> if no such race is known.
   */
  public Race getRace(String typeName) {
    return world.rules.getRace(StringID.create(typeName));
  }

  /**
   * The RegionType with the given name.
   * 
   * @param typeName
   * @return The RegionType or <code>null</code> if no such Region is known.
   */
  public RegionType getRegionType(String typeName) {
    return world.rules.getRegionType(StringID.create(typeName));
  }

  /**
   * Returns the region of the active unit or container.
   * 
   * @return the region of the active unit or container or <code>null</code> if there is no active
   *         unit or container
   */
  public Region getCurrentRegion() {
    if (unit != null)
      return unit.getRegion();
    else if (container != null && (container instanceof HasRegion))
      return ((HasRegion) container).getRegion();
    else
      return null;
  }

  /**
   * Return a region with the given name. <b>Attention:</b> There may be more than one region with
   * the given name. In that case there is no guarantee which of those regions is returned.
   * 
   * @param regionName
   * @return A region with the given name or <code>null</code> if no such region exists.
   */
  public Region getRegion(String regionName) {
    for (Region r : world.getRegions())
      if (regionName.equals(r.getName()))
        return r;
    return null;
  }

  /**
   * Returns the unit object of the given unit with the given id.
   * 
   * @return A unit with the given name or <code>null</code> if no such unit exists.
   */
  public Unit getUnit(String unitId) {
    return world.getUnit(UnitID.createUnitID(unitId, world.base));
  }

  /**
   * Returns the faction object with the given id.
   * 
   * @return A faction with the given name or <code>null</code> if no such faction exists.
   */
  public Faction getFaction(String factionID) {
    return world.getFaction(IntegerID.create(factionID));
  }

  /**
   * Returns the building object with the given id.
   * 
   * @return A building with the given name or <code>null</code> if no such building exists.
   */
  public Building getBuilding(String buildingID) {
    return world.getBuilding(IntegerID.create(buildingID));
  }

  /**
   * Returns the ship object with the given id.
   * 
   * @return A ship with the given name or <code>null</code> if no such ship exists.
   */
  public Ship getShip(String shipID) {
    return world.getShip(IntegerID.create(shipID));
  }

  /**
   * Returns the unit with the given Unit ID in the current region.
   * 
   * @param unitId non-<code>null</code>
   * @return the unit with the given Unit ID in the current region or <code>null</code> if no such
   *         unit is in the current region.
   * @throws NullPointerException if Unit ID is null
   */
  public Unit getUnitInRegion(String unitId) {
    if (getCurrentRegion() != null)
      return (getCurrentRegion()).getUnit(UnitID.createUnitID(unitId, world.base));
    else
      return null;
  }

  /**
   * Returns the unit with the given Unit-ID in the given region.
   * 
   * @param region non-<code>null</code>
   * @param unitId non-<code>null</code>
   * @return the unit with the given Unit-ID in the given region or <code>null</code> if no such
   *         unit is in the given region.
   */
  public Unit getUnitInRegion(Region region, String unitId) {
    return region.getUnit(UnitID.createUnitID(unitId, world.base));
  }

  /**
   * Returns true, if the current unit is in the region with the given name.
   * 
   * @param regionName non-<code>null</code>
   * @return <code>true</code>, if the current unit is in the region with the given name,
   *         <code>null</code> if the current unit is <code>null</code>.
   */
  public boolean isUnitInRegion(String regionName) {
    if (unit != null)
      return regionName.equalsIgnoreCase(unit.getRegion().getName());
    else
      return false;
  }

  /**
   * Returns true, if the current unit is in the region with the given name.
   * 
   * @param regionName non-<code>null</code>
   * @deprecated use {@link #isUnitInRegion(String)}
   */
  @Deprecated
  public boolean unitIsInRegion(String regionName) {
    return isUnitInRegion(regionName);
  }

  /**
   * Returns <code>true</code>, if the unit with unitId is in the same region as the active unit and
   * perception skill of the current unit' faction is greater than the other unit.
   * 
   * @return {@link NullPointerException} if there is no active unit.
   */
  public boolean unitPerceivesOtherUnit(String unitId) {
    if (unit == null)
      throw new NullPointerException();
    Unit otherunit = getUnitInRegion(unitId);
    if (otherunit == null)
      return false;
    return getRegionSkillLevel(unit.getRegion(), unit.getFaction(), world.rules
        .getSkillType(EresseaConstants.S_WAHRNEHMUNG)) >= otherunit.getSkill(
        world.rules.getSkillType(EresseaConstants.S_TARNUNG)).getLevel();
  }

  /**
   * Returns the maximum skill level of any unit of a faction in a region.
   * 
   * @param region
   * @param faction
   * @param skill
   * @return the maximum skill level in the specified skill of any unit of the specified faction in
   *         the specified region.
   * @throws NullPointerException if any of the arguments is <code>null</code>
   */
  public int getRegionSkillLevel(Region region, Faction faction, SkillType skill) {
    if (faction == null || skill == null)
      throw new NullPointerException();
    int level = 0;
    for (Unit u : region.units())
      if (u.getFaction() == faction) {
        level = Math.max(level, u.getSkill(skill).getLevel());
      }
    return level;
  }

  /**
   * Returns the skill level of a unit. For example getLevel(unit,"Unterhaltung")
   */
  public int getLevel(Unit unit, String skillName) {
    Collection<Skill> skills = unit.getSkills();
    if (skills != null) {
      for (Skill skill : skills) {
        if (skill.getSkillType().getName().equalsIgnoreCase(skillName))
          return skill.getLevel();
      }
    }
    return 0;
  }

  /**
   * Returns the number of items of a unit with the given item name. For example: int horses =
   * getItemCount(unit,"Pferd") returns the number of horses of the given unit.
   */
  public int getItemCount(Unit unit, String itemTypeName) {
    Collection<Item> items = unit.getItems();
    if (items != null) {
      for (Item item : items) {
        if (item.getItemType().getName().equalsIgnoreCase(itemTypeName))
          return item.getAmount();
      }
    }
    return 0;
  }

  /**
   * This method returns the amount of silver of the given unit. This is a shortcut for
   * getItemCount(unit,"Silber")
   */
  public int getSilver(Unit unit) {
    return getItemCount(unit, "Silber");
  }

  /**
   * Returns the number of items of a unit with the given item name after execution of orders. For
   * example: int horses = getModifiedItemCount(unit,"Pferd") returns the number of horses of the
   * given unit.
   */
  public int getModifiedItemCount(Unit unit, String itemTypeName) {
    Collection<Item> items = unit.getModifiedItems();
    if (items != null) {
      for (Item item : items) {
        if (item.getItemType().getName().equalsIgnoreCase(itemTypeName))
          return item.getAmount();
      }
    }
    return 0;
  }

  /**
   * Returns the luxury item for the given unit that you can purchase.
   */
  public ItemType getRegionLuxuryItem(Region region) {
    if (region == null)
      return null;
    Map<StringID, LuxuryPrice> prices = region.getPrices();
    for (LuxuryPrice price : prices.values()) {
      if (price.getPrice() < 0)
        return price.getItemType();
    }
    return null;
  }

  /**
   * Returns the available amount for a resource, e.g. getResourceAmount("B�ume").
   * 
   * @param resourceName
   * @return the available amount for a resource
   */
  public int getResourceAmount(String resourceName) {
    RegionResource r = getRegionResource(resourceName);
    if (r != null)
      return r.getAmount();
    else
      return 0;
  }

  /**
   * Returns the current required skill level ("depth") for a resource, e.g.
   * getResourceLevel("Eisen").
   * 
   * @param resourceName
   * @return The current skill level for a resource. {@link Integer#MAX_VALUE} if there is no info
   *         about the resource.
   */
  public int getResourceLevel(String resourceName) {
    RegionResource r = getRegionResource(resourceName);
    if (r != null)
      return r.getSkillLevel();
    else
      return Integer.MAX_VALUE;
  }

  /**
   * Returns a RegionResource object for the resource with the specified name.
   * 
   * @param resourceName
   * @return a RegionResource object for the resource with the specified name or <code>null</code>
   *         if the resource doesn't exist.
   */
  public RegionResource getRegionResource(String resourceName) {
    return getCurrentRegion().getResource(getItemType(resourceName));
  }

  /**
   * Returns the number of persons in this unit.
   */
  public int getPersons(Unit unit) {
    return unit.getPersons();
  }

  /**
   * Adds an order to the current unit.
   */
  public void addOrder(String order) {
    addOrder(unit, order);
  }

  /**
   * Adds an order to the given unit.
   */
  public void addOrder(Unit unit, String order) {
    if (unit == null)
      return;
    unit.addOrder(order, false, 0);
  }

  /**
   * Sets the command for the current unit and replaces all given commands.
   */
  public void setOrder(String order) {
    setOrder(unit, order);
  }

  /**
   * Sets the command for the given unit and replaces all given commands.
   */
  public void setOrder(Unit unit, String order) {
    if (unit == null)
      return;
    List<String> orders = new ArrayList<String>();
    orders.add(order);
    unit.setOrders(orders);
  }

  /**
   * Returns an order in the order locale of the specified unit's faction.
   * 
   * @see EresseaConstants
   * @param unit This order will be converted into this unit's faction's order locale
   * @param orderConstant Use one of the EresseaConstants.O_... constants here
   * @return A localized order constant. If no translation can be found, the orderConstant is
   *         returned.
   */
  public String getOrderTranslation(Unit unit, String orderConstant) {
    return Resources.getOrderTranslation(orderConstant, unit.getFaction().getLocale());
  }

  /**
   * Returns a line like <code>GIVE receiver [EACH] amount item</code> in the locale of the given
   * unit.
   * 
   * @param unit
   * @param receiver
   * @param item
   * @param amount If <code>amount == Integer.MAX_VALUE</code>, amount is replaced by ALL
   * @param each
   * @return a line like <code>GIVE receiver [EACH] amount item</code>.
   */
  public String getGiveOrder(Unit unit, String receiver, String item, int amount, boolean each) {
    return Resources.getOrderTranslation(EresseaConstants.O_GIVE, unit.getFaction().getLocale())
        + " "
        + receiver
        + (each ? " "
            + Resources.getOrderTranslation(EresseaConstants.O_EACH, unit.getFaction().getLocale())
            + " " : " ")
        + (amount == Integer.MAX_VALUE ? getOrderTranslation(unit, EresseaConstants.O_ALL) : amount)
        + " " + item;
  }

  /**
   * Returns a line like <code>RESERVE [EACH] amount item</code>.
   * 
   * @param unit
   * @param item
   * @param amount
   * @param each
   * @return a line like <code>GIVE receiver [EACH] amount item</code>.
   */
  public String getReserveOrder(Unit unit, String item, int amount, boolean each) {
    return getOrderTranslation(unit, EresseaConstants.O_RESERVE)
        + (each ? " " + getOrderTranslation(unit, EresseaConstants.O_EACH) + " " : " ") + amount
        + " " + item;
  }

  /**
   * This method tries to find out, if the current unit has a weapon and a skill to use this weapon.
   */
  public boolean isSoldier() {
    Collection<Item> items = unit.getItems();
    ItemCategory weapons = world.rules.getItemCategory(StringID.create("weapons"), false);
    if (weapons == null) {
      // we don't know something about weapons.
      ExtendedCommandsHelper.log.info("World has no weapons rules");
      return false;
    }

    for (Item item : items) {
      ItemCategory itemCategory = item.getItemType().getCategory();
      if (itemCategory == null) {

        continue;
      }
      if (itemCategory.equals(weapons)) {
        ExtendedCommandsHelper.log.info("Unit has a weapon");
        // ah, a weapon...
        Skill useSkill = item.getItemType().getUseSkill();
        if (useSkill != null) {
          ExtendedCommandsHelper.log.info("Skill needed " + useSkill.getName());
          // okay, has the unit the skill?
          for (Skill skill : unit.getSkills()) {
            ExtendedCommandsHelper.log.info("Skill " + skill.getName());
            if (useSkill.getSkillType().equals(skill.getSkillType())) {
              ExtendedCommandsHelper.log.info("Unit is a soldier.");
              return true;
            }
          }
        }
      }
    }
    ExtendedCommandsHelper.log.info("Unit is not a soldier.");
    return false;
  }

  /**
   * This method returns a list of all regions in the given world. It's a workaround for the complex
   * iteration thru the map.
   * 
   * @deprecated This method is inefficient. Use world.getRegions();
   */
  @Deprecated
  public List<Region> getRegions(GameData world) {
    return new ArrayList<Region>(world.getRegions());
  }

  /**
   * Searches the best path from the current position of an unit to the given region. <br/>
   * This method is only useful for persons on land!
   * 
   * @param unit The unit that should go to another region
   * @param destination The destination region which should be reached
   * @param speed The desired speed per week. This value is ignored! This method uses no speed.
   * @param makeRoute If true, then this unit returns to the current region.
   * @deprecated Use {@link #getPathToRegion(Unit, Region, boolean, boolean)}
   */
  @Deprecated
  public String getPathToRegion(Unit unit, Region destination, int speed, boolean makeRoute) {
    return getPathToRegion(unit, destination, false, makeRoute);
  }

  /**
   * Searches the best path from the current position of an unit to the given region. <br/>
   * This method is only useful for persons on land!
   * 
   * @param unit The unit that should go to another region
   * @param destination The destination region which should be reached
   * @param useSpeed If this is <code>true</code>, this method uses the current calculated ship
   *          range. Otherwise, no range is used.
   * @param makeRoute If true, then this unit returns to the current region.
   */
  public String getPathToRegion(Unit unit, Region destination, boolean useSpeed, boolean makeRoute) {
    List<String> orders =
        (new UnitRoutePlanner()).getOrders(unit, world, unit.getRegion().getID(), destination
            .getCoordinate(), null, true, true, makeRoute, false);
    if (orders.size() == 1)
      return orders.get(0);

    StringBuilder result = new StringBuilder();
    for (String order : orders) {
      result.append(order).append("\n");
    }
    return result.toString();
  }

  /**
   * Searches the best path from the current position of a ship to the given region. <br/>
   * This method is only useful for ships!
   * 
   * @param ship The ship that should go to another region
   * @param destination The destination region which should be reached
   * @param speed The desired speed per week. This value is ignored! This method uses no speed.
   * @param makeRoute If true, then this unit returns to the current region.
   * @deprecated Use {@link #getPathToRegion(Ship, Region, boolean, boolean)}
   */
  @Deprecated
  public String getPathToRegion(Ship ship, Region destination, int speed, boolean makeRoute) {
    return getPathToRegion(ship, destination, false, makeRoute);
  }

  /**
   * Searches the best path from the current position of a ship to the given region. <br/>
   * This method is only useful for ships!
   * 
   * @param ship The ship that should go to another region
   * @param destination The destination region which should be reached
   * @param useSpeed If this is <code>true</code>, this method uses the current calculated ship
   *          range. Otherwise, no range is used.
   * @param makeRoute If true, then this unit returns to the current region.
   */
  public String getPathToRegion(Ship ship, Region destination, boolean useSpeed, boolean makeRoute) {
    List<String> orders =
        (new ShipRoutePlanner()).getOrders(ship, world, ship.getRegion().getCoordinate(),
            destination.getCoordinate(), null, true, useSpeed, makeRoute, false);
    if (orders.size() == 1)
      return orders.get(0);

    StringBuilder result = new StringBuilder();
    for (String order : orders) {
      result.append(order).append("\n");
    }
    return result.toString();
  }

  /**
   * This method reads the orders of a unit and extracts all lines with the syntax '//
   * extcmds:"<key>":value'. This method returns always a map and never null.
   */
  public Map<String, String> getConfiguration(Unit unit) {
    Map<String, String> configuration = new HashMap<String, String>();
    if (unit == null)
      return configuration;

    Collection<String> orders = unit.getOrders();
    if (orders == null)
      return configuration;
    for (String order : orders) {
      if (order == null) {
        continue;
      }
      if (order.startsWith("// extcmds:")) {
        // okay, we found a line with the configuration
        String line = order.substring("// extcmds:".length() + 1);
        if (line.indexOf(":") > 0) {
          String key = line.substring(0, line.indexOf(":"));
          String value = line.substring(line.indexOf(":"));
          key = key.replaceAll("\"", "");
          key = key.replaceAll(":", "");
          configuration.put(key, value);
        }
      }
    }

    return configuration;
  }

  /**
   * This method writes the configuration of a unit into its orders based on the syntax: '//
   * extcmds:"<key>":value'
   */
  public void setConfiguration(Unit unit, Map<String, String> configuration) {
    if (unit == null)
      return;
    if (configuration == null)
      return;
    for (String key : configuration.keySet()) {
      String value = configuration.get(key);
      addOrder("// extcmds:\"" + key + "\":" + value);
    }
  }

  /**
   * This method notifies the GUI that the unit's orders have changed. Call it whenever you change
   * orders of a unit from a script. You don't have to call it when you change the unit's orders
   * from the unit's own script.
   * 
   * @param u The unit that shall be updated in the GUI.
   */
  public void updateUnit(Unit u) {
    u.setOrdersChanged(true);
    client.getDispatcher().fire(new UnitOrdersEvent(this, u));
  }

  /**
   * Returns <code>true</code> if the unit u has an ExtendedCommands script.
   */
  public boolean hasScript(Unit u) {
    for (MagellanPlugIn plugin : client.getPlugIns()) {
      if (plugin instanceof ExtendedCommands) {
        ExtendedCommandsPlugIn exPlugin = (ExtendedCommandsPlugIn) plugin;
        return exPlugin.getExtendedCommands().getCommands(u) != null;
      }
    }
    return false;
  }

  /**
   * Returns <code>true</code> if the container c has an ExtendedCommands script.
   */
  public boolean hasScript(UnitContainer c) {
    for (MagellanPlugIn plugin : client.getPlugIns()) {
      if (plugin instanceof ExtendedCommands) {
        ExtendedCommandsPlugIn exPlugin = (ExtendedCommandsPlugIn) plugin;
        return exPlugin.getExtendedCommands().getCommands(c) != null;
      }
    }
    return false;
  }

  /**
   * Returns a game specific rule manager that provides a few utility methods that differ from game
   * to game.
   * 
   * @return
   */
  public GameSpecificRules getGameSpecificRules() {
    return world.getGameSpecificStuff().getGameSpecificRules();
  }

  /**
   * Returns a MovementEvaluator that provides utility methods concerning the movement of units.
   * 
   * @return
   */
  public MovementEvaluator getMovementEvaluator() {
    return world.getGameSpecificStuff().getMovementEvaluator();
  }
}
