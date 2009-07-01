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

import magellan.library.GameData;
import magellan.library.completion.Completer;
import magellan.library.completion.CompleterSettingsProvider;
import magellan.library.completion.OrderParser;


/**
 * All the stuff needed for Allanon.
 *
 * @author $Author: $
 * @version $Revision: 242 $
 */
public class AllanonSpecificStuff extends EresseaSpecificStuff {

  
  private MovementEvaluator movementEvaluator;
  private GameSpecificRules gameSpecificRules;
  private String name = "Allanon";


  public AllanonSpecificStuff() {
    super();
  }

  /**
   * @see magellan.library.gamebinding.GameSpecificStuff#postProcess(magellan.library.GameData)
   */
	@Override
  public void postProcess(GameData data) {
		AllanonPostProcessor.getSingleton().postProcess(data);
	}

  /**
   * @see magellan.library.gamebinding.GameSpecificStuff#postProcessAfterTrustlevelChange(magellan.library.GameData)
   */
	@Override
  public void postProcessAfterTrustlevelChange(GameData data) {
	  AllanonPostProcessor.getSingleton().postProcessAfterTrustlevelChange(data);
	}

  /**
   * @see magellan.library.gamebinding.GameSpecificStuff#getOrderChanger()
   */
	@Override
  public OrderChanger getOrderChanger() {
		return AllanonOrderChanger.getSingleton();
	}

  /**
   * @see magellan.library.gamebinding.GameSpecificStuff#getRelationFactory()
   */
	@Override
  public RelationFactory getRelationFactory() {
    return AllanonRelationFactory.getSingleton();
//		if (relationFactory == null) {
//      relationFactory = new AllanonRelationFactory(rules);
//    }
//
//    return relationFactory;
	}

  /**
   * @see magellan.library.gamebinding.GameSpecificStuff#getMovementEvaluator()
   */
	@Override
  public MovementEvaluator getMovementEvaluator() {
	  if (movementEvaluator == null) {
      movementEvaluator = new AllanonMovementEvaluator(getRules());
    }

    return movementEvaluator;
	}

  /**
   * @see magellan.library.gamebinding.GameSpecificStuff#getCompleter(magellan.library.GameData, magellan.library.completion.CompleterSettingsProvider)
   */
  @Override
  public Completer getCompleter(GameData data, CompleterSettingsProvider csp) {
		return new AllanonOrderCompleter(data, csp);
	}

  /**
   * @see magellan.library.gamebinding.GameSpecificStuff#getOrderParser(magellan.library.GameData)
   */
	@Override
  public OrderParser getOrderParser(GameData data) {
		return new AllanonOrderParser(data);
	}
  
  /**
   * Delivers the Allanon specific Message Renderer (as of CR VERSION 41)
   * @param data - A GameData object to enrich the messages with names of units, regions ,...
   * @return the new AllanonMessageRenderer for rendering ONE message 
   * 
   * @see magellan.library.gamebinding.GameSpecificStuff#getMessageRenderer(magellan.library.GameData)
   */
  @Override
  public MessageRenderer getMessageRenderer(GameData data) {
    return new AllanonMessageRenderer(data);
  }
  
  /**
   * @see magellan.library.gamebinding.GameSpecificStuff#getMapMergeEvaluator()
   */
  @Override
  public MapMergeEvaluator getMapMergeEvaluator() {
    return AllanonMapMergeEvaluator.getSingleton();
  }
  
  /**
   * @see magellan.library.gamebinding.GameSpecificStuff#getOrderWriter()
   */
  @Override
  public GameSpecificOrderWriter getOrderWriter() {
    return AllanonOrderWriter.getSingleton();
  }

  /**
   * @see magellan.library.gamebinding.GameSpecificStuff#getGameSpecificRules()
   */
  @Override
  public GameSpecificRules getGameSpecificRules() {
    if (gameSpecificRules == null) {
      gameSpecificRules = new AllanonGameSpecificRules(getRules());
    }

    return gameSpecificRules;
  }
  
  public String getName() {
    return name ;
  }
  
}
