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

package magellan.library.relation;

import magellan.library.Unit;
import magellan.library.UnitContainer;
import magellan.library.rules.ItemType;

/**
 * A relation indicating that a unit pays maintenance of a building or ship.
 */
public class MaintenanceRelation extends UnitRelation {
  /** Maintenance costs */
  public int costs;

  /**
   * The maintained building.
   */
  public UnitContainer container;

  public ItemType itemType;

  public boolean warning;

  /**
   * Creates a new MaintenanceRelation object.
   * 
   * @param unit The maintaining unit
   * @param container the maintained building
   * @param amount The costs in silver
   * @param itemType
   * @param line The line in the source's orders
   * @param warning
   */
  public MaintenanceRelation(Unit unit, UnitContainer container, int amount, ItemType itemType,
      int line, boolean warning) {
    super(unit, line);
    this.container = container;
    costs = amount;
    this.itemType = itemType;
    this.warning = warning;
  }

  /**
   * @see magellan.library.relation.UnitRelation#add()
   */
  @Override
  public void add() {
    super.add();
    container.addRelation(this);
  }
}
