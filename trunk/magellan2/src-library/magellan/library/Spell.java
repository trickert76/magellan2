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

package magellan.library;

import java.util.Map;

import magellan.library.utils.SpellSyntax;

/**
 * Container class for a spell based on its representation in a cr version >=
 * 42.
 */
public interface Spell extends Described, Localized {

  /**
   * Returns the integer serving as the block id in the cr.
   */
  public int getBlockID();

  /**
   * Sets the integer serving as the block id in the cr.
   */
  public void setBlockID(int id);

  /**
   * Returns the level of this spell which indicates the lowest skill level a
   * mage must have to be able to cast this spell.
   */
  public int getLevel();

  /**
   * Sets the level of this spell which indicates the lowest skill level a mage
   * must have to be able to cast this spell.
   */
  public void setLevel(int level);

  /**
   * Returns the rank of this spell.
   */
  public int getRank();

  /**
   * Sets the rank of this spell.
   */
  public void setRank(int rank);

  /**
   * Returns the class attribute of this spell or <code>null</code>.
   */
  public String getType();

  /**
   * Sets the class attribute of this spell.
   */
  public void setType(String type);

  /**
   * Returns true if this spell has effect on ocean
   */
  public boolean getOnOcean();

  /**
   * Sets if this spell has effect on oceans
   */
  public void setOnOcean(boolean onOcean);

  /**
   * Returns true if this spell has effect on ships
   */
  public boolean getOnShip();

  /**
   * DOCUMENT-ME
   */
  public void setOnShip(boolean onShip);

  /**
   * DOCUMENT-ME
   */
  public boolean getIsFamiliar();

  /**
   * DOCUMENT-ME
   */
  public void setIsFamiliar(boolean isFamiliar);

  /**
   * Returns true if this spell has far effects
   */
  public boolean getIsFar();

  /**
   * DOCUMENT-ME
   */
  public void setIsFar(boolean isFar);

  /**
   * Returns the components of this spell as Strings.
   */
  public Map<String,String> getComponents();

  /**
   * DOCUMENT-ME
   */
  public void setComponents(Map<String, String> components);

  /**
   * @see Object#toString()
   */
  public String toString();

  /**
   * Returns a name for this spell's type.
   */
  public String getTypeName();

  /**
   * A human readable string with information about the syntax of the spell or <code>null</code>.
   */
  public String getSyntaxString();

  /**
   * Enno in e-client about the syntax:
   * <ul> 
   * <li>'c' = Zeichenkette</li> 
   * <li>'k' = REGION|EINHEIT|STUFE|SCHIFF|GEBAEUDE </li>
   * <li>'i' = Zahl </li>
   * <li>'s' = Schiffsnummer </li>
   * <li>'b' = Gebaeudenummer </li>
   * <li>'r' = Regionskoordinaten (x, y) </li>
   * <li>'u' = Einheit </li>
   * <li>'+' = Wiederholung des vorangehenden Parameters </li>
   * <li>'?' = vorangegangener Parameter</li>
   * </ul>
   * ist nicht zwingend Syntaxcheks, die der Server auf dieser Basis macht, sind
   * nicht perfekt; es ist notwendig, aber nicht hinreichend, dass die Syntax
   * erfuellt wird. Aber in den vielen Faellen kann man damit schonmal sagen,
   * was denn falsch war.
   * 
   * @return the syntax or <code>null</code>
   */
  public String getSyntax();

  /**
   * @param syntax
   *          the syntax to set
   */
  public void setSyntax(String syntax);

  /**
   * returns the spellsyntax object of this spell
   * 
   * @return a SpellSyntax object
   */
  public SpellSyntax getSpellSyntax();
}
