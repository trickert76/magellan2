/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package magellan.library.gamebinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import magellan.library.IntegerID;
import magellan.library.Item;
import magellan.library.Message;
import magellan.library.Region;
import magellan.library.Rules;
import magellan.library.Skill;
import magellan.library.Unit;
import magellan.library.UnitID;
import magellan.library.rules.ItemType;
import magellan.library.rules.MessageType;
import magellan.library.rules.Race;
import magellan.library.utils.Regions;
import magellan.library.utils.logging.Logger;

/**
 * @author $Author: $
 * @version $Revision: 396 $
 */
public class EresseaMovementEvaluator implements MovementEvaluator {
  private static Logger log = Logger.getInstance(EresseaMovementEvaluator.class);

  private Rules rules;
  private Collection<ItemType> horseTypes;

  private MessageType transportMessageType = new MessageType(IntegerID.create(891175669));

  protected EresseaMovementEvaluator(Rules rules) {
    this.rules = rules;
    horseTypes = new ArrayList<ItemType>(2);
    for (ItemType type : rules.getItemTypes()) {
      if (type.isHorse()) {
        horseTypes.add(type);
      }
    }
  }

  /**
   * Returns the maximum payload in GE 100 of this unit when it travels by horse. Horses, carts and
   * persons are taken into account for this calculation. If the unit has a sufficient skill in
   * horse riding but there are too many carts for the horses, the weight of the additional carts
   * are also already considered.
   * 
   * @return the payload in GE 100, CAP_NO_HORSES if the unit does not possess horses or
   *         CAP_UNSKILLED if the unit is not sufficiently skilled in horse riding to travel on
   *         horseback.
   */
  public int getPayloadOnHorse(Unit unit) {
    int capacity = 0;
    int horses = getHorses(unit);

    if (horses <= 0)
      return MovementEvaluator.CAP_NO_HORSES;

    int skillLevel = 0;
    Skill s = unit.getModifiedSkill(rules.getSkillType(EresseaConstants.S_REITEN, true));

    if (s != null) {
      skillLevel = s.getLevel();
    }

    if (horses > (skillLevel * unit.getModifiedPersons() * 2))
      return MovementEvaluator.CAP_UNSKILLED;

    int carts = 0;
    Item i = unit.getModifiedItem(rules.getItemType(EresseaConstants.I_CART, true));

    if (i != null) {
      carts = i.getAmount();
    }

    int horsesWithoutCarts = horses - (carts * 2);

    Race race = getRace(unit);

    if (horsesWithoutCarts >= 0) {
      capacity =
          (((carts * 140) + (horsesWithoutCarts * 20)) * 100)
              - (((int) ((race.getWeight()) * 100)) * unit.getModifiedPersons());
    } else {
      int cartsWithoutHorses = carts - (horses / 2);
      horsesWithoutCarts = horses % 2;
      capacity =
          (((((carts - cartsWithoutHorses) * 140) + (horsesWithoutCarts * 20)) - (cartsWithoutHorses * 40)) * 100)
              - (((int) ((race.getWeight()) * 100)) * unit.getModifiedPersons());
    }
    // Fiete 20070421 (Runde 519)
    // GOTS not active when riding! (tested)
    // return respectGOTS(unit, capacity);
    return capacity;
  }

  protected int getHorses(Unit unit) {
    int horses = 0;
    for (ItemType horseType : horseTypes) {
      Item i = unit.getModifiedItem(horseType);

      if (i != null) {
        horses += i.getAmount();
      }
    }
    return horses;
  }

  /**
   * Returns the maximum payload in GE 100 of this unit when it travels on foot. Horses, carts and
   * persons are taken into account for this calculation. If the unit has a sufficient skill in
   * horse riding but there are too many carts for the horses, the weight of the additional carts
   * are also already considered. The calculation also takes into account that trolls can tow carts.
   * 
   * @return the payload in GE 100, CAP_UNSKILLED if the unit is not sufficiently skilled in horse
   *         riding to travel on horseback.
   */
  public int getPayloadOnFoot(Unit unit) {
    int capacity = 0;
    int horses = getHorses(unit);

    if (horses < 0) {
      horses = 0;
    }

    int skillLevel = 0;
    Skill s = unit.getModifiedSkill(rules.getSkillType(EresseaConstants.S_REITEN, true));

    if (s != null) {
      skillLevel = s.getLevel();
    }

    if (horses > ((skillLevel * unit.getModifiedPersons() * 4) + unit.getModifiedPersons()))
      // too many horses
      return MovementEvaluator.CAP_UNSKILLED;

    int carts = 0;
    Item i = unit.getModifiedItem(rules.getItemType(EresseaConstants.I_CART, true));

    if (i != null) {
      carts = i.getAmount();
    }

    if (carts < 0) {
      carts = 0;
    }

    int horsesWithoutCarts = 0;
    int cartsWithoutHorses = 0;

    if (skillLevel == 0) {
      // can't use carts!!!
      horsesWithoutCarts = horses;
      cartsWithoutHorses = carts;
    } else if (carts > (horses / 2)) {
      // too many carts
      cartsWithoutHorses = carts - (horses / 2);
    } else {
      // too many horses (or exactly right number)
      horsesWithoutCarts = horses - (carts * 2);
    }

    Race race = getRace(unit);

    if ((race == null) || (race.getID().equals(EresseaConstants.R_TROLLE) == false)) {
      capacity =
          (((((carts - cartsWithoutHorses) * 140) + (horsesWithoutCarts * 20)) - (cartsWithoutHorses * 40)) * 100)
              + (((int) ((race == null ? 10 : race.getCapacity()) * 100)) * unit
                  .getModifiedPersons());
    } else {
      int horsesMasteredPerPerson = (skillLevel * 4) + 1;
      int trollsMasteringHorses = horses / horsesMasteredPerPerson;

      if ((horses % horsesMasteredPerPerson) != 0) {
        trollsMasteringHorses++;
      }

      int cartsTowedByTrolls =
          Math.min((unit.getModifiedPersons() - trollsMasteringHorses) / 4, cartsWithoutHorses);
      int trollsTowingCarts = cartsTowedByTrolls * 4;
      int untowedCarts = cartsWithoutHorses - cartsTowedByTrolls;
      capacity =
          (((((carts - untowedCarts) * 140) + (horsesWithoutCarts * 20)) - (untowedCarts * 40)) * 100)
              + (((int) (race.getCapacity() * 100)) * (unit.getModifiedPersons() - trollsTowingCarts));
    }

    return respectGOTS(unit, capacity);
  }

  private int respectGOTS(Unit unit, int capacity) {
    Item gots = unit.getModifiedItem(rules.getItemType(EresseaConstants.I_GOTS, true));

    if (gots == null)
      return capacity;

    int multiplier = Math.max(0, Math.min(unit.getPersons(), gots.getAmount()));
    Race race = getRace(unit);

    if ((multiplier == 0) || (race == null))
      return capacity;

    // increase capacity by 49*unit.race.capacity per GOTS
    return capacity + (multiplier * (49 * (int) (race.getCapacity() * 100)));
  }

  /**
   * @deprecated Use {@link #getModifiedRadius(Unit)}
   */
  @Deprecated
  public int getRadius(Unit u) {
    return getRadius(u, false);
  }

  /**
   * Returns the number of regions this unit is able to travel within one turn based on the riding
   * skill, horses, carts and load of this unit.
   * 
   * @deprecated Use {@link #getModifiedRadius(Unit, boolean)}.
   */
  @Deprecated
  public int getRadius(Unit u, boolean onRoad) {
    // pavkovic 2003.10.02: use modified load here...int load = getLoad();
    int load = getModifiedLoad(u);
    int payload = getPayloadOnHorse(u);

    if ((payload >= 0) && ((payload - load) >= 0))
      return onRoad ? 3 : 2;
    else {
      payload = getPayloadOnFoot(u);

      if ((payload >= 0) && ((payload - load) >= 0))
        return onRoad ? 2 : 1;
      else
        return 0;
    }
  }

  private Race getRace(Unit unit) {
    Race race = unit.getRace();

    return race;
  }

  /**
   * Returns the weight of all items of this unit that are not horses or carts in silver
   * 
   * @see magellan.library.gamebinding.MovementEvaluator#getLoad(magellan.library.Unit)
   */
  public int getLoad(Unit unit) {
    return getLoad(unit, unit.getItems());
  }

  /**
   * Returns the weight of all items of this unit that are not horses or carts in silver based on
   * the modified items plus all passengers modified weight.
   * 
   * @see magellan.library.gamebinding.MovementEvaluator#getModifiedLoad(magellan.library.Unit)
   */
  public int getModifiedLoad(Unit unit) {
    int load = getLoad(unit, unit.getModifiedItems());

    // also take care of passengers
    for (Unit passenger : unit.getPassengers()) {
      load += getModifiedWeight(passenger);
    }

    return load;
  }

  /**
   * Returns the weight of all items of this unit that are not horses or carts in silver based on
   * the specified items.
   */
  private int getLoad(Unit unit, Collection<Item> items) {
    int load = 0;
    ItemType cart = rules.getItemType(EresseaConstants.I_CART, true);
    // darcduck 2007-10-31: take care of bags of negative weight
    ItemType bonw = rules.getItemType(EresseaConstants.I_BONW, true);

    for (Item i : items) {
      if (!i.getItemType().isHorse() && !i.getItemType().equals(cart)) {
        // pavkovic 2003.09.10: only take care about (possibly) modified items with positive amount
        if (i.getAmount() > 0) {
          load += (((int) (i.getItemType().getWeight() * 100)) * i.getAmount());
        }
      }
      // darcduck 2007-10-31: take care of bags of negative weight
      if (i.getItemType().equals(bonw)) {
        load -= getBonwLoad(unit, items, i);
      }
    }

    return load;
  }

  /**
   * Returns the load in GE 100 of the bag of negative weight (bonw). This might be 0 if nothing can
   * be stored in the bag up to 200 per bag. Items are only considered to be stored in the bonw if
   * this is set in the rules. ItemType returns this in method isStoreableInBonw()
   * 
   * @return the load of the bonw in GE 100.
   */
  private int getBonwLoad(Unit unit, Collection<Item> items, Item i_bonw) {
    final int I_BONW_CAP = 20000;
    int bonwload = 0;
    int bonwcap = 0;

    if (i_bonw != null) {
      bonwcap = i_bonw.getAmount() * I_BONW_CAP;

      for (Item i : items) {
        if (bonwload >= bonwcap) {
          break;
        }
        if ((i.getAmount() > 0) && (i.getItemType().isStoreableInBonw())) {
          bonwload += (((int) (i.getItemType().getWeight() * 100)) * i.getAmount());
        }
      }
      bonwload = Math.min(bonwcap, bonwload);
    }
    return bonwload;
  }

  /**
   * The initial weight of the unit as it appear in the report. This is the eressea version used to
   * calculate the weight if the information is not available in the report.
   * 
   * @return the weight of the unit in silver (GE 100).
   */
  public int getWeight(Unit unit) {
    if (unit.isWeightWellKnown())
      return unit.getSimpleWeight();
    else
      return getWeight(unit, unit.getItems(), unit.getPersons());
  }

  /**
   * The modified weight is calculated from the modified number of persons and the modified items.
   * Due to some eressea dependencies this is done in this class.
   * 
   * @return the modified weight of the unit in silver (GE 100).
   */
  public int getModifiedWeight(Unit unit) {
    int weight = getWeight(unit, unit.getModifiedItems(), unit.getModifiedPersons());

    /*
     * if we have a weight tag in the report we know the current exact weight via getWeight() but we
     * may not know the weight of some items or races which results in a to less calculated
     * (modified) weight. to overcome this, we do a delta calculation here, then we have a higher
     * chance of a correct size, at least when noting is given away or received.
     */
    if (unit.isWeightWellKnown()) {
      weight += unit.getSimpleWeight();
      weight -= getWeight(unit);
    }
    return weight;
  }

  /**
   * Returns the weight of the unit given the given collection of items and number of persons. Bags
   * of negative weight are respected.
   */
  private int getWeight(Unit unit, Collection<Item> items, int persons) {
    int weight = 0;
    float personWeight = getRace(unit).getWeight();
    // darcduck 2007-10-31: take care of bags of negative weight
    ItemType bonw = rules.getItemType(EresseaConstants.I_BONW, true);

    for (Item item : items) {
      // pavkovic 2003.09.10: only take care about (possibly) modified items with positive amount
      if (item.getAmount() > 0) {
        weight += (item.getAmount() * (int) (item.getItemType().getWeight() * 100));
      }
      // darcduck 2007-10-31: take care of bags of negative weight
      if (item.getItemType().equals(bonw)) {
        weight -= getBonwLoad(unit, items, item);
      }
    }

    weight += (persons * (int) (personWeight * 100));

    return weight;
  }

  /**
   * @see magellan.library.gamebinding.MovementEvaluator#getModifiedRadius(magellan.library.Unit)
   */
  public int getModifiedRadius(Unit unit) {
    return getModifiedRadius(unit, false);
  }

  /**
   * @see magellan.library.gamebinding.MovementEvaluator#getModifiedRadius(magellan.library.Unit,
   *      boolean)
   */
  public int getModifiedRadius(Unit unit, boolean onRoad) {
    int load = getModifiedLoad(unit);

    int payload = getPayloadOnHorse(unit);

    if ((payload >= 0) && (payload >= load))
      return onRoad ? 3 : 2;

    payload = getPayloadOnFoot(unit);

    if ((payload >= 0) && (payload >= load))
      return onRoad ? 2 : 1;

    return 0;
  }

  /**
   * @see magellan.library.gamebinding.MovementEvaluator#getModifiedRadius(magellan.library.Unit,
   *      java.util.List)
   */
  public int getModifiedRadius(Unit unit, List<Region> path) {
    if (path.size() == 0)
      return 0;
    Region start = path.iterator().next();
    if (start != unit.getRegion())
      throw new IllegalArgumentException("unit not in first path region");

    int radius = getModifiedRadius(unit, false), streetRadius = getModifiedRadius(unit, true);
    int etappe = 0;
    Region lastRegion = null;
    boolean road = true;
    int length = 0;
    for (Region r : path) {
      if (lastRegion != null) {
        if (lastRegion == r)
          return length;
        else {
          etappe++;
          if (!Regions.isCompleteRoadConnection(lastRegion, r)) {
            road = false;
          }
          if (road) {
            if (etappe >= streetRadius)
              return length;
          } else {
            if (etappe >= radius)
              return length;
          }
        }
      }
      lastRegion = r;
      length++;
    }
    return length;
  }

  /**
   * @see magellan.library.gamebinding.MovementEvaluator#getDistance(magellan.library.Unit,
   *      java.util.List)
   */
  public int getDistance(Unit unit, List<Region> path) {
    if (path.size() == 0)
      return 0;
    Region start = path.iterator().next();
    if (start != unit.getRegion())
      throw new IllegalArgumentException("unit not in first path region");

    int radius = getModifiedRadius(unit, false), streetRadius = getModifiedRadius(unit, true);
    int weeks = 0, etappe = 0;
    Region lastRegion = null;
    boolean road = true;
    for (Region r : path) {
      if (lastRegion != null) {
        if (lastRegion == r) {
          if (etappe > 0) {
            weeks++;
            etappe = 0;
            road = true;
          }
        } else {
          etappe++;
          if (!Regions.isCompleteRoadConnection(lastRegion, r)) {
            road = false;
          }
          if (road) {
            if (etappe >= streetRadius) {
              weeks++;
              etappe = 0;
            }
          } else {
            if (etappe == radius) {
              weeks++;
              etappe = 0;
              road = true;
            } else if (etappe >= streetRadius) {
              weeks++;
              etappe = 1;
            }
          }
        }
      }
      lastRegion = r;
    }
    if (etappe > 0) {
      ++weeks;
    }
    return weeks;
  }

  public MessageType getTransportMessageType() {
    return transportMessageType;
  }

  /**
   * Returns <code>true</code> if the unit's past movement was passive (transported, shipped...)
   * 
   * @param u
   * @return <code>true</code> if there is evidence that the unit's past movement was passive
   *         (transported, shipped...)
   */
  public boolean isPastMovementPassive(Unit u) {
    if (u.getShip() != null) {
      if (u.equals(u.getShip().getOwnerUnit())) {
        // unit is on ship and the owner
        if (log.isDebugEnabled()) {
          log.debug("PathCellRenderer(" + u + "):false on ship");
        }

        return false;
      }

      // unit is on a ship and not the owner
      if (log.isDebugEnabled()) {
        log.debug("PathCellRenderer(" + u + "):true on ship");
      }

      return true;
    }

    // we assume a transportation to be passive, if
    // there is no message of type 891175669
    if (u.getFaction() == null) {
      if (log.isDebugEnabled()) {
        log.debug("PathCellRenderer(" + u + "):false no faction");
      }

      return false;
    }

    if (u.getFaction().getMessages() == null) {
      // faction has no message at all
      if (log.isDebugEnabled()) {
        log.debug("PathCellRenderer(" + u + "):false no faction");
      }

      return true;
    }

    for (Message m : u.getFaction().getMessages()) {
      if (log.isDebugEnabled()) {
        if (getTransportMessageType().equals(m.getMessageType())) {
          log.debug("PathCellRenderer(" + u + ") Message " + m);

          if ((m.getAttributes() != null) && (m.getAttributes().get("unit") != null)) {
            log.debug("PathCellRenderer(" + u + ") Unit   " + m.getAttributes().get("unit"));
            // FIXME actually it should be creatUnitID(*, 10, data.base), but it doesn't matter
            // here
            log.debug("PathCellRenderer(" + u + ") UnitID "
                + UnitID.createUnitID(m.getAttributes().get("unit"), 10));
          }
        }
      }

      if (getTransportMessageType().equals(m.getMessageType()) && (m.getAttributes() != null)
          && (m.getAttributes().get("unit") != null)
          && u.getID().equals(UnitID.createUnitID(m.getAttributes().get("unit"), 10))) { // FIXME
                                                                                         // 10,data.base
        // found a transport message; this is only valid in
        // units with active movement
        if (log.isDebugEnabled()) {
          log.debug("PathCellRenderer(" + u + "):false with message " + m);
        }

        return false;
      }
    }

    if (log.isDebugEnabled()) {
      log.debug("PathCellRenderer(" + u + "):true with messages");
    }

    return true;
  }

  protected Rules getRules() {
    return rules;
  }
}
