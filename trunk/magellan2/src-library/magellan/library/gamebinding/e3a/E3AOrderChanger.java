/*
 * Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe, Stefan Goetz, Sebastian Pappert, Klaas
 * Prause, Enno Rehling, Sebastian Tusk, Ulrich Kuester, Ilja Pavkovic This file is part of the
 * Eressea Java Code Base, see the file LICENSING for the licensing information applying to this
 * file.
 */

package magellan.library.gamebinding.e3a;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import magellan.library.Rules;
import magellan.library.Unit;
import magellan.library.gamebinding.EresseaConstants;
import magellan.library.gamebinding.EresseaOrderChanger;
import magellan.library.utils.Resources;

/**
 * @author $Author: $
 * @version $Revision: 305 $
 */
public class E3AOrderChanger extends EresseaOrderChanger {

  protected E3AOrderChanger(Rules rules) {
    super(rules);
  }

  /**
   * E2K9 has no concept of stealth. So this changes nothing.
   */
  @Override
  public void addHideOrder(Unit unit, String level) {

  }

  /**
   * FIXME (stm) must honor primary and secondary races
   */
  @Override
  public void addRecruitOrder(Unit unit, int i) {
    String order =
        Resources.getOrderTranslation(EresseaConstants.O_RECRUIT) + " " + String.valueOf(i);
    unit.addOrders(order);
  }

  /**
   * Just change number and description.
   * 
   * @param unit The affected unit.
   */
  @Override
  public void addMultipleHideOrder(Unit unit) {
    List<String> orders = new LinkedList<String>();
    orders.add(Resources.getOrderTranslation(EresseaConstants.O_NUMBER) + " "
        + Resources.getOrderTranslation(EresseaConstants.O_UNIT) + " ");
    orders.add(Resources.getOrderTranslation(EresseaConstants.O_NAME) + " "
        + Resources.getOrderTranslation(EresseaConstants.O_UNIT) + " \"\"");
    orders.add(Resources.getOrderTranslation(EresseaConstants.O_DESCRIBE) + " "
        + Resources.getOrderTranslation(EresseaConstants.O_UNIT) + " \"\"");
    orders.add(Resources.getOrderTranslation(EresseaConstants.O_HIDE) + " "
        + Resources.getOrderTranslation(EresseaConstants.O_FACTION));

    if (unit.getShip() != null) {
      orders.add(Resources.getOrderTranslation(EresseaConstants.O_NUMBER) + " "
          + Resources.getOrderTranslation(EresseaConstants.O_SHIP));
      orders.add(Resources.getOrderTranslation(EresseaConstants.O_NAME) + " "
          + Resources.getOrderTranslation(EresseaConstants.O_SHIP) + " \"\"");
      orders.add(Resources.getOrderTranslation(EresseaConstants.O_DESCRIBE) + " "
          + Resources.getOrderTranslation(EresseaConstants.O_SHIP) + " \"\"");
    }

    orders.add("// " + Resources.getOrderTranslation(EresseaConstants.O_NUMBER) + " "
        + Resources.getOrderTranslation(EresseaConstants.O_UNIT) + " " + unit.getID());
    orders.add("// " + Resources.getOrderTranslation(EresseaConstants.O_NAME) + " "
        + Resources.getOrderTranslation(EresseaConstants.O_UNIT) + " \"" + unit.getName() + "\"");

    if (unit.getDescription() != null) {
      orders.add("// " + Resources.getOrderTranslation(EresseaConstants.O_DESCRIBE) + " "
          + Resources.getOrderTranslation(EresseaConstants.O_UNIT) + " \"" + unit.getDescription()
          + "\"");
    }

    if (unit.getShip() != null) {
      orders.add("// " + Resources.getOrderTranslation(EresseaConstants.O_NUMBER) + " "
          + Resources.getOrderTranslation(EresseaConstants.O_SHIP) + " "
          + unit.getShip().getID().toString());
      orders.add("// " + Resources.getOrderTranslation(EresseaConstants.O_NAME) + " "
          + Resources.getOrderTranslation(EresseaConstants.O_SHIP) + " \""
          + unit.getShip().getName() + "\"");

      if (unit.getShip().getDescription() != null) {
        orders.add("// " + Resources.getOrderTranslation(EresseaConstants.O_DESCRIBE) + " "
            + Resources.getOrderTranslation(EresseaConstants.O_SHIP) + " \""
            + unit.getShip().getDescription() + "\"");
      }
    }

    unit.addOrders(orders);
  }

  private ArrayList<String> longOrderTokens;

  /**
   * Returns list of long order tokens in E3.
   * 
   * @see magellan.library.gamebinding.EresseaOrderChanger#getLongOrderTokens()
   */
  @Override
  protected ArrayList<String> getLongOrderTokens() {
    if (this.longOrderTokens == null) {
      this.longOrderTokens = new ArrayList<String>();
      this.longOrderTokens.add(EresseaConstants.O_WORK);
      this.longOrderTokens.add(EresseaConstants.O_ATTACK);
      this.longOrderTokens.add(EresseaConstants.O_STEAL);
      this.longOrderTokens.add(EresseaConstants.O_SIEGE);
      this.longOrderTokens.add(EresseaConstants.O_RIDE);
      this.longOrderTokens.add(EresseaConstants.O_FOLLOW);
      this.longOrderTokens.add(EresseaConstants.O_RESEARCH);
      this.longOrderTokens.add(EresseaConstants.O_BUY);
      this.longOrderTokens.add(EresseaConstants.O_TEACH);
      this.longOrderTokens.add(EresseaConstants.O_LEARN);
      this.longOrderTokens.add(EresseaConstants.O_MAKE);
      this.longOrderTokens.add(EresseaConstants.O_MOVE);
      this.longOrderTokens.add(EresseaConstants.O_PLANT);
      this.longOrderTokens.add(EresseaConstants.O_ROUTE);
      this.longOrderTokens.add(EresseaConstants.O_SABOTAGE);
      this.longOrderTokens.add(EresseaConstants.O_SPY);
      this.longOrderTokens.add(EresseaConstants.O_TAX);
      this.longOrderTokens.add(EresseaConstants.O_ENTERTAIN);
      this.longOrderTokens.add(EresseaConstants.O_SELL);
      this.longOrderTokens.add(EresseaConstants.O_CAST);
      this.longOrderTokens.add(EresseaConstants.O_GROW);
    }
    return this.longOrderTokens;
  }

  private ArrayList<String> longButShortOrderTokens = null;

  /**
   * @see magellan.library.gamebinding.EresseaOrderChanger#getLongButShortOrderTokens()
   */
  @Override
  protected ArrayList<String> getLongButShortOrderTokens() {
    if (this.longButShortOrderTokens == null) {
      this.longButShortOrderTokens = new ArrayList<String>();
      this.longButShortOrderTokens.add(EresseaConstants.O_MAKE + " " + EresseaConstants.O_TEMP);
    }
    return this.longButShortOrderTokens;
  }

}
