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

package magellan.library.tasks;

import java.util.Collection;
import java.util.List;

import magellan.library.Region;
import magellan.library.Unit;
import magellan.library.tasks.Problem.Severity;

/**
 * An Inspector reviews the given resource and returns a list of problems
 */
public interface Inspector {

  /** All lines suppressing problems must start with this prefix. */
  public static final String SUPPRESS_PREFIX = "; @suppressProblem";

  /**
   * Reviews a unit and returns a list of <tt>Problem</tt> objects associated
   * with it.
   */
  public List<Problem> reviewUnit(Unit u);

  /**
   * Reviews a unit and returns the list of <tt>Problem</tt> objects which are
   * of the given type.
   * 
   * @param u
   *          The unit to review
   * @param severity
   *          The type of problems to filter, e.g. Severity.INFORMATION
   */
  public List<Problem> reviewUnit(Unit u, Severity severity);

  /**
   * Reviews a region and returns a list of <code>Problem</code> objects
   * associated with it.
   */
  public List<Problem> reviewRegion(Region r);

  /**
   * Reviews a region and returns the list of <tt>Problem</tt> objects which
   * are of the given type.
   */
  public List<Problem> reviewRegion(Region r, Severity severity);

  /**
   * Modifies the orders such that this problem is not listed by the inspector
   * in the future, i.e. by adding a comment to the source unit's orders. Note
   * that it is in the responsibility of the caller to fire OrderChangedEvents.
   * 
   * @param problem
   * @return Returns a unit whose orders were changed
   */
  public Unit suppress(Problem problem);

  /**
   * Removes all additions made by suppress of this Inspector to the Unit's orders.
   * 
   * @param u
   */
  public void unSuppress(Unit u);


  /**
   * Returns all ProblemTypes this Inspector may return.
   * 
   */
  public Collection<ProblemType> getTypes();

}
