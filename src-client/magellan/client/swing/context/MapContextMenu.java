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

package magellan.client.swing.context;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import magellan.client.Client;
import magellan.client.desktop.MagellanDesktop;
import magellan.client.event.EventDispatcher;
import magellan.client.event.SelectionEvent;
import magellan.client.extern.MagellanPlugIn;
import magellan.client.swing.AddSignDialog;
import magellan.client.swing.MapperPanel;
import magellan.client.swing.map.MapCellRenderer;
import magellan.client.swing.map.Mapper;
import magellan.client.swing.map.RenderingPlane;
import magellan.library.Bookmark;
import magellan.library.BookmarkBuilder;
import magellan.library.CoordinateID;
import magellan.library.GameData;
import magellan.library.Region;
import magellan.library.Selectable;
import magellan.library.event.GameDataEvent;
import magellan.library.utils.MagellanFactory;
import magellan.library.utils.Resources;
import magellan.library.utils.logging.Logger;

/**
 * A context menu for the map. Currently providing copy of the name and the coordinates of the
 * region.
 * 
 * @author Ulrich K�ster
 */
public class MapContextMenu extends JPopupMenu implements ContextObserver {

  private static final Logger log = Logger.getInstance(MapContextMenu.class);

  // the region on which the menu is inferred
  private Region region;
  private final EventDispatcher dispatcher;

  private final Client client;
  private GameData data;
  private final Properties settings;
  // FIXME (stm) should this class be a GameDataListener?
  private Map<CoordinateID, Region> selectedRegions = new Hashtable<CoordinateID, Region>();
  private Collection<Region> armystatsSel = new HashSet<Region>();
  private static final String RKEY = "MAGELLAN.RENDERER";
  private static final String TKEY = "MAGELLAN.TOOLTIP";
  protected JMenuItem name;
  protected JMenuItem changeSelState;
  protected JMenuItem copyNameID;
  protected JMenuItem setOriginItem;
  protected JMenuItem changeHotSpot;
  protected JMenu signs;
  protected JMenu levelSelect;
  protected JMenu jumpToHotSpot;
  protected JMenuItem armystats;
  protected JMenu renderer;
  protected JMenu tooltips;
  protected ActionListener rListener;
  protected ActionListener tListener;
  protected Mapper source;

  protected PathfinderMapContextMenu pathFinder;

  private Collection<MapContextMenuProvider> externalMapContectMenuProvider = null;

  /**
   * Creates a new MapContextMenu object.
   * 
   * @param aClient The client owning the menu
   * @param aDispatcher The event dispatcher for events generated by this class
   * @param settings The Properties for saving and getting settings
   */
  public MapContextMenu(Client aClient, EventDispatcher aDispatcher, Properties settings) {
    client = aClient;
    dispatcher = aDispatcher;
    this.settings = settings;

    // this.client = client;
    rListener = new RendererActionListener();
    tListener = new TooltipActionListener();

    name = new JMenuItem();
    name.setEnabled(false);
    add(name);

    changeSelState =
        new JMenuItem(Resources.get("context.mapcontextmenu.menu.changeselectionstate"));
    changeSelState.setEnabled(false);
    changeSelState.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changeSelectionState();
      }
    });
    add(changeSelState);

    copyNameID = new JMenuItem(Resources.get("context.mapcontextmenu.menu.copyidandname.caption"));
    copyNameID.setEnabled(false);
    copyNameID.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        copyNameID();
      }
    });
    add(copyNameID);

    setOriginItem = new JMenuItem(Resources.get("context.mapcontextmenu.menu.setorigin"));
    setOriginItem.setEnabled(false);
    setOriginItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        CoordinateID c = CoordinateID.create(region.getCoordinate());
        client.setOrigin(c);
        // client.setOrigin fires already the event..
        // dispatcher.fire(new GameDataEvent(this, data));
        setCursor(Cursor.getDefaultCursor());
      }
    });
    add(setOriginItem);

    changeHotSpot = new JMenuItem(Resources.get("context.mapcontextmenu.menu.changehotspot"));
    changeHotSpot.setEnabled(false);
    changeHotSpot.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changeHotSpot();
      }
    });
    add(changeHotSpot);

    armystats = new JMenuItem(Resources.get("context.mapcontextmenu.menu.armystats"));
    armystats.setEnabled(false);

    final EventDispatcher ed = aDispatcher;
    final Properties set = settings;
    armystats.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          new magellan.client.swing.ArmyStatsDialog(new Frame(), ed, data, set, armystatsSel)
              .setVisible(true);
        } catch (Exception exc) {
          // somebody elses problem
          log.warn(exc);
        }
      }
    });
    add(armystats);

    levelSelect = new JMenu(Resources.get("context.mapcontextmenu.menu.levelselect"));
    levelSelect.setEnabled(false);
    add(levelSelect);

    jumpToHotSpot = new JMenu(Resources.get("context.mapcontextmenu.menu.jumptohotspot"));
    jumpToHotSpot.setEnabled(false);
    add(jumpToHotSpot);

    signs = new JMenu(Resources.get("context.mapcontextmenu.menu.signs"));
    add(signs);
    addSeparator();

    tooltips = new JMenu(Resources.get("context.mapcontextmenu.menu.tooltips"));
    tooltips.setEnabled(false);
    add(tooltips);

    renderer = new JMenu(Resources.get("context.mapcontextmenu.menu.renderer"));
    renderer.setEnabled(false);
    add(renderer);

    pathFinder = new PathfinderMapContextMenu(dispatcher);
    pathFinder.setEnabled(false);
    add(pathFinder);
    dispatcher.addSelectionListener(pathFinder);
    dispatcher.addGameDataListener(pathFinder);

    initContextMenuProviders();

  }

  private void initContextMenuProviders() {
    Collection<MapContextMenuProvider> cmpList = getMapContextMenuProviders();
    if (!cmpList.isEmpty()) {
      addSeparator();
    }
    for (MapContextMenuProvider cmp : cmpList) {
      add(cmp.createMapContextMenu(dispatcher, data));
      if (externalMapContectMenuProvider == null) {
        externalMapContectMenuProvider = new ArrayList<MapContextMenuProvider>();
      }
      externalMapContectMenuProvider.add(cmp);
    }

  }

  /**
   * Searchs for Map Context Menu Providers in the plugins and adds them to the menu.
   */
  private Collection<MapContextMenuProvider> getMapContextMenuProviders() {
    Collection<MapContextMenuProvider> cmpList = new ArrayList<MapContextMenuProvider>();
    for (MagellanPlugIn plugIn : client.getPlugIns()) {
      if (plugIn instanceof MapContextMenuProvider) {
        cmpList.add((MapContextMenuProvider) plugIn);
      }
    }
    return cmpList;
  }

  /**
   * DOCUMENT-ME
   */
  public void init(Region r, Collection<Region> selectedRegions) {
    this.selectedRegions.clear();
    for (Region reg : selectedRegions) {
      this.selectedRegions.put(reg.getID(), reg);
    }
    armystatsSel.clear();
    armystatsSel.addAll(selectedRegions);
    armystatsSel.add(r);
    region = r;
    setLabel(r.toString());
    name.setText(r.toString());
    changeSelState.setEnabled(true);
    copyNameID.setEnabled(true);
    setOriginItem.setEnabled(true);
    changeHotSpot.setEnabled(true);
    armystats.setEnabled(true);
    signs.setEnabled(true);
    updateLevelSelect();
    updateJumpToHotSpot();
    updateSigns();

    pathFinder.updateMenu(r);

    updateMapContextMenuProvider(r, null);

  }

  /**
   * Updates the external provided menues (pLugins) r - the region of known or null c - the
   * CoordinateID if region is not know
   */
  private void updateMapContextMenuProvider(Region r, CoordinateID c) {
    if (externalMapContectMenuProvider != null && externalMapContectMenuProvider.size() > 0) {
      for (MapContextMenuProvider plugIn : externalMapContectMenuProvider) {
        if (r != null) {
          plugIn.update(r);
        }
        if (c != null) {
          plugIn.updateUnknownRegion(c);
        }
      }
    }
  }

  /**
   * Right click on an area without an existing region... the assumed CordinateID is provided
   */
  public void clear(CoordinateID c) {
    String s = Resources.get("context.mapcontextmenu.menu.noregion");
    setLabel(s);
    name.setText(s);
    changeSelState.setEnabled(false);
    copyNameID.setEnabled(false);
    setOriginItem.setEnabled(false);
    changeHotSpot.setEnabled(false);
    armystats.setEnabled(false);
    signs.setEnabled(false);
    pathFinder.setEnabled(false);

    updateMapContextMenuProvider(null, c);

  }

  /**
   * Updates the level selector
   */
  private void updateLevelSelect() {
    levelSelect.removeAll();
    if (source.getLevels().size() > 1) {
      levelSelect.setEnabled(true);
      for (Integer actLevel : source.getLevels()) {
        JMenuItem levelSign = new JMenuItem(actLevel.toString());
        levelSign.setActionCommand(actLevel.toString());
        if (actLevel.intValue() != source.getLevel()) {
          levelSign.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              MapperPanel mp =
                  (MapperPanel) client.getDesktop().getManagedComponents().get(
                      MagellanDesktop.MAP_IDENTIFIER);
              mp.setLevel(Integer.parseInt(e.getActionCommand()));
            }
          });
        } else {
          // levelSign.setEnsabled(false);
        }
        levelSelect.add(levelSign);
      }
    } else {
      levelSelect.setEnabled(false);
    }
  }

  /**
   * Updates the level selector
   */
  private void updateJumpToHotSpot() {
    jumpToHotSpot.removeAll();
    if (data != null && data.getBookmarks().size() > 0) {
      jumpToHotSpot.setEnabled(true);
      for (Bookmark h : data.getBookmarks()) {
        JMenuItem bookmarkItem = new JMenuItem(h.toString());
        // levelSign.setActionCommand(h.getObject());
        final Selectable s = h.getObject();
        bookmarkItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            MapperPanel mp =
                (MapperPanel) client.getDesktop().getManagedComponents().get(
                    MagellanDesktop.MAP_IDENTIFIER);
            Bookmark h2 = data.getBookmark(s);
            if (h2 != null) {
              mp.showHotSpot(h2);
            } else {
              MapContextMenu.log.error("Bookmark not found: " + s);
            }
          }
        });

        jumpToHotSpot.add(bookmarkItem);
      }
    } else {
      jumpToHotSpot.setEnabled(false);
    }
  }

  /**
   * sets the option (submenuentries) in the signmenu depends on status of signs of actual region
   * Fiete
   */
  private void updateSigns() {
    signs.removeAll();
    // add or delete
    if (region.getSigns() != null && region.getSigns().size() > 0) {
      JMenuItem delSign = new JMenuItem(Resources.get("context.mapcontextmenu.menu.signs.selsign"));
      delSign.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          delSign();
        }
      });
      signs.add(delSign);
    } else {
      JMenuItem addSign = new JMenuItem(Resources.get("context.mapcontextmenu.menu.signs.addsign"));
      addSign.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          addSign();
        }
      });
      signs.add(addSign);
    }

    // remove all
    JMenuItem delAllSigns =
        new JMenuItem(Resources.get("context.mapcontextmenu.menu.signs.selallsigns"));
    delAllSigns.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        delAllSigns();
      }
    });
    signs.add(delAllSigns);
  }

  /**
   * deletes all signs of the actual region
   */
  private void delSign() {
    region.clearSigns();
    updateMap();
  }

  /**
   * deletes all signs of the current gamedata (CR)
   */
  private void delAllSigns() {
    if (JOptionPane.showConfirmDialog(this, Resources
        .get("context.mapcontextmenu.delsigns.confirm.message"), Resources
            .get("context.mapcontextmenu.delsigns.confirm.title"),
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      for (Region region2 : data.getRegions()) {
        (region2).clearSigns();
      }
    }
    updateMap();
  }

  /**
   * fires gamedatachanged to refresh map (and everything..;-))
   */
  private void updateMap() {
    dispatcher.fire(new GameDataEvent(this, data));
  }

  /**
   * adds a new sign - using the AddSignDialog
   */
  private void addSign() {
    AddSignDialog addSignDialog = new AddSignDialog(client, true, settings, dispatcher, region);
    addSignDialog.setVisible(true);
  }

  /**
   * Update the menu items from the sources tooltip definition.
   * 
   * @param src The current source is set to this value
   */
  public void updateTooltips(Mapper src) {
    source = src;
    tooltips.removeAll();

    Iterator<String> it = src.getAllTooltipDefinitions().iterator();

    while (it.hasNext()) {
      String[] tip = new String[2];
      tip[0] = it.next();
      tip[1] = it.next();
      String s = tip[0] + ": " + tip[1];

      if (s.length() > 25) {
        s = s.substring(0, 23) + "...";
      }

      JMenuItem item = new JMenuItem(s);
      item.addActionListener(tListener);
      item.putClientProperty(MapContextMenu.TKEY, tip);
      tooltips.add(item);
    }

    tooltips.setEnabled(tooltips.getItemCount() > 0);
  }

  /**
   * DOCUMENT-ME
   */
  public void updateRenderers(Mapper src) {
    source = src;
    renderer.removeAll();

    // add renderers that are context changeable
    boolean added = false;

    for (RenderingPlane rp : src.getPlanes()) {
      if (rp == null) {
        continue;
      }
      MapCellRenderer r = rp.getRenderer();
      if ((r != null) && (r instanceof ContextChangeable)) {
        JMenuItem mi = ((ContextChangeable) r).getContextAdapter();

        if (mi instanceof JMenu) {
          renderer.add(mi);
        } else {
          JMenu help = new JMenu(r.getName());
          help.add(mi);
          renderer.add(help);
        }

        ((ContextChangeable) r).setContextObserver(this);
        added = true;
      }
    }

    // add renderer choosers
    if (added) {
      renderer.addSeparator();
    }

    for (RenderingPlane rp : src.getPlanes()) {
      if (rp == null) {
        continue;
      }
      JMenu help = new JMenu(rp.getName());
      Collection<MapCellRenderer> rs = src.getRenderers(rp.getIndex());
      boolean addedi = false;

      if (rs != null) {
        addedi = true;

        JMenuItem item = new JMenuItem(Resources.get("context.mapcontextmenu.menu.renderer.none"));
        item.setEnabled(rp.getRenderer() != null);
        item.putClientProperty(MapContextMenu.RKEY, Integer.valueOf(rp.getIndex()));
        item.addActionListener(rListener);
        help.add(item);

        Iterator<MapCellRenderer> it2 = rs.iterator();

        while (it2.hasNext()) {
          MapCellRenderer mcp = it2.next();
          item = new JMenuItem(mcp.getName());
          item.setEnabled(mcp != rp.getRenderer());
          item.addActionListener(rListener);
          item.putClientProperty(MapContextMenu.RKEY, mcp);
          help.add(item);
        }
      }

      help.setEnabled(addedi);
      renderer.add(help);
    }

    renderer.setEnabled(true);
  }

  /**
   * Changes th game data.
   */
  public void setGameData(GameData d) {
    data = d;
  }

  /**
   * Changes the selection state of the region
   */
  private void changeSelectionState() {
    if (selectedRegions.containsValue(region)) {
      selectedRegions.remove(region.getID());
    } else {
      selectedRegions.put(region.getID(), region);
    }

    data.setSelectedRegionCoordinates(selectedRegions);
    dispatcher.fire(SelectionEvent.create(this, selectedRegions.values()));
  }

  /**
   * Copies name and coordinates to the sytem clipboard.
   */
  private void copyNameID() {
    String toCopy = region.toString();
    if (region.hasUID() && region.getUID() >= 0) {
      toCopy += " (" + Integer.toString((int) region.getUID(), data.base).replace("l", "L") + ")";
    }
    StringSelection strSel = new StringSelection(toCopy);

    Clipboard cb = getToolkit().getSystemClipboard();
    cb.setContents(strSel, null);
  }

  /**
   * Sets or deletes an hotspot
   */
  private void changeHotSpot() {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    boolean found = false;

    for (Bookmark h : data.getBookmarks()) {
      if (h.getObject() == region || h.getObject().equals(region.getCoordinate())) {
        found = true;
        data.removeBookmark(h.getObject());
        break;
      }
    }

    if (!found) {
      BookmarkBuilder h = MagellanFactory.createBookmark();
      h.setObject(region);
      h.setName(region.toString());
      data.addBookmark(h.getBookmark());
    }

    dispatcher.fire(new GameDataEvent(this, data));
    setCursor(Cursor.getDefaultCursor());
  }

  /**
   * @see magellan.client.swing.context.ContextObserver#contextDataChanged()
   */
  public void contextDataChanged() {
    if (source != null) {
      Mapper.setRenderContextChanged(true);
      source.repaint();
    }
  }

  protected class TooltipActionListener implements ActionListener {
    /**
     * Changes the tooltip
     */
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
      if ((source != null) && (actionEvent.getSource() instanceof JMenuItem)) {
        JMenuItem src = (JMenuItem) actionEvent.getSource();
        Object obj = src.getClientProperty(MapContextMenu.TKEY);

        if (obj == null)
          return;

        String[] tip = (String[]) obj;
        source.setTooltipDefinition(tip[0], tip[1]);
      }
    }
  }

  /**
   * Reacts to actions from the Renderer submenu of the map context menu.
   */
  protected class RendererActionListener implements ActionListener {
    /**
     * Changes the renderer.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
      if ((source != null) && (actionEvent.getSource() instanceof JMenuItem)) {
        JMenuItem src = (JMenuItem) actionEvent.getSource();

        try {
          Object obj = src.getClientProperty(MapContextMenu.RKEY);

          if (obj == null)
            return;

          if (obj instanceof MapCellRenderer) {
            MapCellRenderer mcp = (MapCellRenderer) obj;
            source.setRenderer(mcp);
          } else if (obj instanceof Integer) {
            source.setRenderer(null, ((Integer) obj).intValue());
          }

          Mapper.setRenderContextChanged(true);
          source.repaint();
        } catch (Exception exc) {
          log.warn(exc);
        }
      }
    }
  }
}
