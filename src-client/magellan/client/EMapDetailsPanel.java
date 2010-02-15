/*
 * Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe, Stefan Goetz, Sebastian Pappert, Klaas
 * Prause, Enno Rehling, Sebastian Tusk, Ulrich Kuester, Ilja Pavkovic This file is part of the
 * Eressea Java Code Base, see the file LICENSING for the licensing information applying to this
 * file.
 */

package magellan.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;

import magellan.client.completion.AutoCompletion;
import magellan.client.desktop.DesktopEnvironment;
import magellan.client.desktop.ShortcutListener;
import magellan.client.event.EventDispatcher;
import magellan.client.event.SelectionEvent;
import magellan.client.event.SelectionListener;
import magellan.client.event.UnitOrdersEvent;
import magellan.client.event.UnitOrdersListener;
import magellan.client.preferences.DetailsViewPreferences;
import magellan.client.swing.BasicRegionPanel;
import magellan.client.swing.FactionStatsPanel;
import magellan.client.swing.InternationalizedDataPanel;
import magellan.client.swing.MenuProvider;
import magellan.client.swing.RoutingDialog;
import magellan.client.swing.completion.MultiEditorOrderEditorList;
import magellan.client.swing.context.ContextFactory;
import magellan.client.swing.context.UnitCapacityContextMenu;
import magellan.client.swing.context.UnitContextMenu;
import magellan.client.swing.preferences.PreferencesAdapter;
import magellan.client.swing.preferences.PreferencesFactory;
import magellan.client.swing.tree.CellObject;
import magellan.client.swing.tree.CellRenderer;
import magellan.client.swing.tree.ContextManager;
import magellan.client.swing.tree.CopyTree;
import magellan.client.swing.tree.ItemCategoryNodeWrapper;
import magellan.client.swing.tree.ItemNodeWrapper;
import magellan.client.swing.tree.NodeWrapperDrawPolicy;
import magellan.client.swing.tree.NodeWrapperFactory;
import magellan.client.swing.tree.PotionNodeWrapper;
import magellan.client.swing.tree.RegionNodeWrapper;
import magellan.client.swing.tree.SimpleNodeWrapper;
import magellan.client.swing.tree.TreeUpdate;
import magellan.client.swing.tree.UnitCommentNodeWrapper;
import magellan.client.swing.tree.UnitContainerCommentNodeWrapper;
import magellan.client.swing.tree.UnitContainerNodeWrapper;
import magellan.client.swing.tree.UnitListNodeWrapper;
import magellan.client.swing.tree.UnitNodeWrapper;
import magellan.client.swing.tree.UnitRelationNodeWrapper;
import magellan.client.swing.tree.UnitRelationNodeWrapper2;
import magellan.client.utils.Units;
import magellan.library.Alliance;
import magellan.library.AllianceGroup;
import magellan.library.Border;
import magellan.library.Building;
import magellan.library.CombatSpell;
import magellan.library.Described;
import magellan.library.EntityID;
import magellan.library.Faction;
import magellan.library.GameData;
import magellan.library.Group;
import magellan.library.ID;
import magellan.library.Island;
import magellan.library.Item;
import magellan.library.LuxuryPrice;
import magellan.library.Named;
import magellan.library.Potion;
import magellan.library.Region;
import magellan.library.RegionResource;
import magellan.library.Rules;
import magellan.library.Scheme;
import magellan.library.Ship;
import magellan.library.Skill;
import magellan.library.Spell;
import magellan.library.StringID;
import magellan.library.Unique;
import magellan.library.Unit;
import magellan.library.UnitContainer;
import magellan.library.UnitID;
import magellan.library.ZeroUnit;
import magellan.library.event.GameDataEvent;
import magellan.library.gamebinding.EresseaConstants;
import magellan.library.gamebinding.GameSpecificRules;
import magellan.library.gamebinding.GameSpecificStuff;
import magellan.library.relation.ControlRelation;
import magellan.library.relation.ItemTransferRelation;
import magellan.library.relation.PersonTransferRelation;
import magellan.library.relation.ReserveRelation;
import magellan.library.relation.UnitRelation;
import magellan.library.relation.UnitTransferRelation;
import magellan.library.rules.CastleType;
import magellan.library.rules.ConstructibleType;
import magellan.library.rules.ItemCategory;
import magellan.library.rules.ItemType;
import magellan.library.rules.Race;
import magellan.library.rules.ShipType;
import magellan.library.rules.SkillCategory;
import magellan.library.rules.SkillType;
import magellan.library.rules.UnitContainerType;
import magellan.library.utils.Direction;
import magellan.library.utils.Locales;
import magellan.library.utils.MagellanFactory;
import magellan.library.utils.PropertiesHelper;
import magellan.library.utils.Resources;
import magellan.library.utils.ShipRoutePlanner;
import magellan.library.utils.Taggable;
import magellan.library.utils.Umlaut;
import magellan.library.utils.Utils;
import magellan.library.utils.comparator.BestSkillComparator;
import magellan.library.utils.comparator.IDComparator;
import magellan.library.utils.comparator.NameComparator;
import magellan.library.utils.comparator.PotionLevelComparator;
import magellan.library.utils.comparator.SkillComparator;
import magellan.library.utils.comparator.SkillRankComparator;
import magellan.library.utils.comparator.SortIndexComparator;
import magellan.library.utils.comparator.SpecifiedSkillTypeSkillComparator;
import magellan.library.utils.comparator.SpellLevelComparator;
import magellan.library.utils.comparator.ToStringComparator;
import magellan.library.utils.comparator.UnitSkillComparator;
import magellan.library.utils.filters.CollectionFilters;
import magellan.library.utils.logging.Logger;

/**
 * Shows details about units, regions or whatever object is currently selected.
 * 
 * @author $Author: $
 * @version $Revision: 390 $
 */
public class EMapDetailsPanel extends InternationalizedDataPanel implements SelectionListener,
    ShortcutListener, ActionListener, TreeUpdate, PreferencesFactory, MenuProvider {
  private static final Logger log = Logger.getInstance(EMapDetailsPanel.class);

  // GUI elements
  private JPanel nameDescPanel = null;
  private JPanel pnlRegionInfoTree = null;
  private MultiEditorOrderEditorList editor;

  private JTextArea name = null;
  private JTextArea description = null;
  private DefaultTreeModel treeModel = null;
  private DefaultMutableTreeNode rootNode = null;
  private CopyTree tree = null;
  private AutoCompletion orders = null;
  private Units unitsTools = null;

  /**
   * A list containing nodewrapper objects, the expansion state of nodes contained in this list can
   * be automatically stored and restored
   */
  private Collection<NodeWrapper> myExpandableNodes = new LinkedList<NodeWrapper>();

  /** The currently displayed object */
  private SelectionEvent currentSelection = null;

  /** split pane */
  private JSplitPane topSplitPane;

  /** split pane */
  private JSplitPane bottomSplitPane;

  private static final NumberFormat weightNumberFormat = NumberFormat.getNumberInstance();
  protected NodeWrapperFactory nodeWrapperFactory;
  private List<KeyStroke> shortCuts;
  protected Collection<String> excludeTags = new LinkedList<String>();
  protected AbstractButton addTag;
  protected AbstractButton removeTag;
  protected Container tagContainer;
  protected Container treeContainer;

  protected boolean showTagButtons = false;
  protected boolean allowCustomIcons = true;
  private boolean compactLayout = false;

  protected ContextManager contextManager;
  protected StealthContextFactory stealthContext;
  protected CombatStateContextFactory combatContext;
  protected CommentContextFactory commentContext;
  protected UnitCommentContextFactory unitCommentContext;
  protected DetailsUnitContextFactory unitContext;
  protected RelationContextFactory relationContext;
  protected UndoManager undoMgr;
  protected BasicRegionPanel regionPanel;

  // pre-initialize this comparator so it is not created over and
  // over again when needed
  private Comparator<Unit> sortIndexComparator =
      new SortIndexComparator<Unit>(IDComparator.DEFAULT);
  private final StringID rironID = EresseaConstants.I_RIRON; // StringID.create("Eisen");
  private final StringID rlaenID = EresseaConstants.I_RLAEN; // StringID.create("Laen");
  private final StringID rtreesID = EresseaConstants.I_TREES; // StringID.create("Baeume");
  private final StringID rmallornID = EresseaConstants.I_RMALLORN; // StringID.create("Mallorn");
  private final StringID rsproutsID = EresseaConstants.I_SPROUTS; // StringID.create("Schoesslinge");
  private final StringID rmallornSproutsID = EresseaConstants.I_MALLORNSPROUTS; // StringID.create("Mallornsch�sslinge");
  private final StringID rstonesID = EresseaConstants.I_RSTONES; // StringID.create("Steine");
  private final StringID rhorsesID = EresseaConstants.I_RHORSES; // StringID.create("Pferde");
  private final StringID rsilverID = EresseaConstants.I_RSILVER; // StringID.create("Silber");
  private final StringID rpeasantsID = EresseaConstants.I_PEASANTS; // StringID.create("Bauern");

  private GameSpecificRules gameRules = null;

  private GameSpecificStuff gameSpecStuff = null;

  /**
   * Creates a new EMapDetailsPanel object.
   */
  public EMapDetailsPanel(EventDispatcher d, GameData data, Properties p, UndoManager _undoMgr) {
    super(d, data, p);
    initGUI(_undoMgr);
    init(data);
  }

  private void initGUI(UndoManager _undoMgr) {
    // store undomanager
    undoMgr = _undoMgr;

    nodeWrapperFactory =
        new NodeWrapperFactory(settings, "EMapDetailsPanel", Resources
            .get("emapdetailspanel.wrapperfactory.title"));
    nodeWrapperFactory.setSource(this);

    // to get the pref-adapter
    Unit temp = MagellanFactory.createUnit(UnitID.createUnitID(0, 10));
    nodeWrapperFactory.createUnitNodeWrapper(temp);
    nodeWrapperFactory.createSkillNodeWrapper(temp, new Skill(
        new SkillType(StringID.create("Test")), 0, 0, 0, false), null);
    nodeWrapperFactory.createItemNodeWrapper(new Item(new ItemType(StringID.create("Test")), 0));
    nodeWrapperFactory.createSimpleNodeWrapper(null, (Collection<String>) null);

    EMapDetailsPanel.weightNumberFormat.setMaximumFractionDigits(2);
    EMapDetailsPanel.weightNumberFormat.setMinimumFractionDigits(0);
    unitsTools = (data != null) ? new Units(data.rules) : new Units(null);
    dispatcher.addSelectionListener(this);

    // name text area
    name = new JTextArea();
    name.setLineWrap(false);
    // name.setFont(new Font("SansSerif", Font.PLAIN, name.getFont().getSize()));
    name.setCursor(new Cursor(Cursor.TEXT_CURSOR));
    name.setEditable(false);
    name.getDocument().addUndoableEditListener(_undoMgr);
    name.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if ((name.isEditable() == false) || (getDisplayedObject() == null))
          return;

        if (getDisplayedObject() instanceof Unit) {
          Unit u = (Unit) getDisplayedObject();

          if ((((u.getName() == null) && (name.getText().equals("") == false)) || ((u.getName() != null) && (name
              .getText().equals(u.getName()) == false)))
              && (isEditAll() || magellan.library.utils.Units.isPrivilegedAndNoSpy(u))
              && !u.ordersAreNull()) {
            // the following code only changes the name
            // right now it is not necessary to refresh the relations; are we sure??
            data.getGameSpecificStuff().getOrderChanger().addNamingOrder(u, name.getText());
            dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, u));

            // if (u.cache != null && u.cache.orderEditor != null) {
            // u.cache.orderEditor.reloadOrders();
            // }
          }
        } else if (getDisplayedObject() instanceof UnitContainer) {
          UnitContainer uc = (UnitContainer) getDisplayedObject();
          Unit modUnit = null;

          if (uc instanceof Faction) {
            modUnit = uc.units().iterator().next();
          } else {
            // use old owner unit (BENENNE before GIB)
            modUnit = uc.getOwnerUnit();
          }

          if (magellan.library.utils.Units.isPrivilegedAndNoSpy(modUnit)
              && !modUnit.ordersAreNull()) {
            if (((uc.getName() == null) && (name.getText().equals("") == false))
                || ((uc.getName() != null) && (name.getText().equals(uc.getName()) == false))) {
              data.getGameSpecificStuff().getOrderChanger().addNamingOrder(modUnit, uc,
                  name.getText());
              dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, modUnit));

              // if (modUnit.cache != null && modUnit.cache.orderEditor != null) {
              // modUnit.cache.orderEditor.reloadOrders();
              // }
            }
          } else {
            JOptionPane.showMessageDialog(((JComponent) e.getSource()).getTopLevelAncestor(),
                Resources.get("emapdetailspanel.msg.cannotrename.text"), Resources
                    .get("emapdetailspanel.error"), javax.swing.JOptionPane.WARNING_MESSAGE);
            tree.grabFocus();
          }
        } else if (getDisplayedObject() instanceof Island) {
          ((Island) getDisplayedObject()).setName(name.getText());
        }
      }
    });

    // description text area
    description = new JTextArea();
    description.setLineWrap(true);
    description.setWrapStyleWord(true);
    description.setFont(new Font("SansSerif", Font.PLAIN, description.getFont().getSize()));
    description.setCursor(new Cursor(Cursor.TEXT_CURSOR));
    description.setEditable(false);
    description.getDocument().addUndoableEditListener(_undoMgr);
    description.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if ((description.isEditable() == false) || (getDisplayedObject() == null))
          return;

        if (getDisplayedObject() instanceof Unit) {
          Unit u = (Unit) getDisplayedObject();

          if ((((u.getDescription() == null) && (description.getText().equals("") == false)) || ((u
              .getDescription() != null) && (description.getText().equals(u.getDescription()) == false)))
              && (isEditAll() || magellan.library.utils.Units.isPrivilegedAndNoSpy(u))
              && !u.ordersAreNull()) {
            String descr = getDescriptionPart(description.getText());
            String privat = getPrivatePart(description.getText());
            data.getGameSpecificStuff().getOrderChanger().addDescribeUnitOrder(u, descr);

            if (((u.getPrivDesc() == null) && (privat.length() > 0))
                || ((u.getPrivDesc() != null) && !privat.equals(u.getPrivDesc()))) {
              data.getGameSpecificStuff().getOrderChanger().addDescribeUnitPrivateOrder(u, privat);
              u.setPrivDesc(privat);
            }

            dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, u));

            // if (u.cache != null && u.cache.orderEditor != null) {
            // u.cache.orderEditor.reloadOrders();
            // }
          }
        } else if (getDisplayedObject() instanceof UnitContainer) {
          UnitContainer uc = (UnitContainer) getDisplayedObject();
          Unit modUnit = null;

          if (uc instanceof Faction) {
            modUnit = uc.units().iterator().next();
          } else {
            // use old owner unit (BENENNE before GIB)
            modUnit = uc.getOwnerUnit();
          }

          if (magellan.library.utils.Units.isPrivilegedAndNoSpy(modUnit)
              && !modUnit.ordersAreNull()) {
            if (((uc.getDescription() == null) && (description.getText().equals("") == false))
                || ((uc.getDescription() != null) && (description.getText().equals(
                    uc.getDescription()) == false))) {
              data.getGameSpecificStuff().getOrderChanger().addDescribeUnitContainerOrder(modUnit,
                  uc, normalizeDescription(description.getText()));
              dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, modUnit));

              // if (modUnit.cache != null && modUnit.cache.orderEditor != null) {
              // modUnit.cache.orderEditor.reloadOrders();
              // }
            }
          } else {
            JOptionPane.showMessageDialog(((JComponent) e.getSource()).getTopLevelAncestor(),
                Resources.get("emapdetailspanel.msg.cannotdescribe.text"), Resources
                    .get("emapdetailspanel.error"), javax.swing.JOptionPane.WARNING_MESSAGE);
            tree.grabFocus();
          }
        } else if (getDisplayedObject() instanceof Island) {
          ((Described) getDisplayedObject()).setDescription(normalizeDescription(description
              .getText()));
        }
      }
    });

    JScrollPane descScrollPane =
        new JScrollPane(description, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    // ClearLook suggests to remove border
    descScrollPane.setBorder(null);

    // panel combining name and description
    nameDescPanel = new JPanel(new BorderLayout(0, 2));
    nameDescPanel.add(name, BorderLayout.NORTH);
    nameDescPanel.add(descScrollPane, BorderLayout.CENTER);
    nameDescPanel.setPreferredSize(new Dimension(100, 100));

    // tree
    rootNode = new DefaultMutableTreeNode(null);
    treeModel = new DefaultTreeModel(rootNode);
    tree = new CopyTree(treeModel);
    tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          handleValueChange();
        }
      }
    });

    // keeping track of selection changes for mySelectedUnits (fiete)
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent tslE) {
        handleTreeSelectionChangeEvent(tslE);
      }
    });

    // bugzilla bug #823 ?
    tree.putClientProperty("JTree.lineStyle", "Angled");

    tree.setRootVisible(false);

    tree.setEditable(true);
    DefaultTreeCellRenderer delegate = (DefaultTreeCellRenderer) tree.getCellRenderer();
    tree.setCellRenderer(new CellRenderer(getMagellanContext()));

    tree.setCellEditor(new DefaultTreeCellEditor(tree, delegate) {
      /**
       * Ensures that only comment cells are editable.
       * 
       * @see javax.swing.tree.DefaultTreeCellEditor#isCellEditable(java.util.EventObject)
       */
      @Override
      public boolean isCellEditable(EventObject anEvent) {
        if (anEvent == null)
          return super.isCellEditable(anEvent);
        DefaultMutableTreeNode selectedNode =
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if ((selectedNode != null)
            && (selectedNode.getUserObject() instanceof UnitContainerCommentNodeWrapper || selectedNode
                .getUserObject() instanceof UnitCommentNodeWrapper))
          return super.isCellEditable(anEvent);
        return false;
      }

    });

    // handle editing of unit container comment edits
    tree.getCellEditor().addCellEditorListener(new CellEditorListener() {
      public void editingCanceled(ChangeEvent e) {
      }

      /**
       * Sets the comment of the UnitContainer if a unit container comment node was edited.
       * 
       * @see javax.swing.event.CellEditorListener#editingStopped(javax.swing.event.ChangeEvent)
       */
      public void editingStopped(ChangeEvent e) {
        DefaultMutableTreeNode selectedNode =
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if ((selectedNode != null)
            && selectedNode.getUserObject() instanceof UnitContainerCommentNodeWrapper) {
          UnitContainerCommentNodeWrapper cnW =
              (UnitContainerCommentNodeWrapper) selectedNode.getUserObject();
          DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
          UnitContainer uc = cnW.getUnitContainer();

          if ((uc != null) && (uc.getComments() != null)) {
            uc.getComments().set(parent.getIndex(selectedNode),
                (String) tree.getCellEditor().getCellEditorValue());
          }
        } else {
          // undo the changes
        }
        refresh();
      }
    });

    // handle editing of unit comment edits
    tree.getCellEditor().addCellEditorListener(new CellEditorListener() {
      public void editingCanceled(ChangeEvent e) {
      }

      /**
       * Sets the comment of the Unit if a unit comment node was edited.
       * 
       * @see javax.swing.event.CellEditorListener#editingStopped(javax.swing.event.ChangeEvent)
       */
      public void editingStopped(ChangeEvent e) {
        DefaultMutableTreeNode selectedNode =
            (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if ((selectedNode != null)
            && selectedNode.getUserObject() instanceof UnitCommentNodeWrapper) {
          UnitCommentNodeWrapper cnW = (UnitCommentNodeWrapper) selectedNode.getUserObject();
          DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
          Unit u = cnW.getUnit();

          if ((u != null) && (u.getComments() != null)) {
            u.getComments().set(parent.getIndex(selectedNode),
                (String) tree.getCellEditor().getCellEditorValue());
          }
          refresh();
        }
      }
    });

    contextManager = new ContextManager(tree, dispatcher);
    stealthContext = new StealthContextFactory();
    combatContext = new CombatStateContextFactory();
    commentContext = new CommentContextFactory();
    unitCommentContext = new UnitCommentContextFactory();
    unitContext = new DetailsUnitContextFactory();
    relationContext = new RelationContextFactory();
    contextManager.putSimpleObject(CommentListNode.class, commentContext);
    contextManager.putSimpleObject(CommentNode.class, commentContext);
    contextManager.putSimpleObject(UnitCommentNode.class, unitCommentContext);
    contextManager.putSimpleObject(UnitCommentListNode.class, unitCommentContext);
    contextManager.putSimpleObject(UnitNodeWrapper.class, unitContext);
    contextManager.putSimpleObject(UnitListNodeWrapper.class, unitContext);
    contextManager.putSimpleObject(DefaultMutableTreeNode.class, unitContext);
    contextManager.putSimpleObject(UnitRelationNodeWrapper.class, relationContext);
    contextManager.putSimpleObject(UnitRelationNodeWrapper2.class, relationContext);

    JScrollPane treeScrollPane = new JScrollPane(tree);

    // ClearLook suggests to remove border
    treeScrollPane.setBorder(null);

    // panel combining the region info and the tree
    pnlRegionInfoTree = new JPanel(new BorderLayout());
    pnlRegionInfoTree.add(regionPanel = new BasicRegionPanel(dispatcher, settings),
        BorderLayout.NORTH);
    pnlRegionInfoTree.add(treeScrollPane, BorderLayout.CENTER);

    tagContainer = new JPanel();
    tagContainer.setLayout(new GridLayout(1, 2));
    treeContainer = pnlRegionInfoTree;

    addTag = new JButton(Resources.get("emapdetailspanel.addtag.caption"));
    addTag.addActionListener(this);
    addTag.setEnabled(false);
    tagContainer.add(addTag);

    removeTag = new JButton(Resources.get("emapdetailspanel.removetag.caption"));
    removeTag.addActionListener(this);
    removeTag.setEnabled(false);
    tagContainer.add(removeTag);

    showTagButtons =
        settings.getProperty("EMapDetailsPanel.ShowTagButtons", "false").equals("true");

    if (showTagButtons) {
      pnlRegionInfoTree.add(tagContainer, BorderLayout.SOUTH);
    }

    allowCustomIcons =
        settings.getProperty("EMapDetailsPanel.AllowCustomIcons", "true").equals("true");

    compactLayout = settings.getProperty("EMapDetailsPanel.CompactLayout", "false").equals("true");

    // split pane combining name, desc & tree
    topSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, nameDescPanel, pnlRegionInfoTree);
    topSplitPane.setOneTouchExpandable(true);

    editor = new MultiEditorOrderEditorList(dispatcher, data, settings, _undoMgr);

    // build auto completion structure
    orders = new AutoCompletion(dispatcher.getMagellanContext());
    orders.attachEditorManager(editor);
    editor.setCompleter(orders);
    shortCuts = new ArrayList<KeyStroke>(3);
    shortCuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_MASK));
    shortCuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_MASK));

    // toggle "limit make completion"
    shortCuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));

    // split pane combining top split pane and orders

    bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, editor);
    bottomSplitPane.setOneTouchExpandable(true);
    setLayout(new GridLayout(1, 0));
    add(bottomSplitPane);

    // adjust split panes
    bottomSplitPane.setDividerLocation(Integer.parseInt(settings.getProperty(
        "EMapDetailsPanel.bottomSplitPane", "400")));
    topSplitPane.setDividerLocation(Integer.parseInt(settings.getProperty(
        "EMapDetailsPanel.topSplitPane", "200")));

    // save split pane changes
    ComponentAdapter splitPaneListener = new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        settings.setProperty("EMapDetailsPanel.bottomSplitPane", ""
            + bottomSplitPane.getDividerLocation());
        settings.setProperty("EMapDetailsPanel.topSplitPane", ""
            + topSplitPane.getDividerLocation());
      }
    };

    treeScrollPane.addComponentListener(splitPaneListener);
    nameDescPanel.addComponentListener(splitPaneListener);
    editor.addComponentListener(splitPaneListener);

    // register for shortcuts
    DesktopEnvironment.registerShortcutListener(this);

    // update this component if the orders of the currently displayed unit changed
    dispatcher.addUnitOrdersListener(new UnitOrdersListener() {
      public void unitOrdersChanged(UnitOrdersEvent e) {
        if (e.getRelatedUnits().contains(EMapDetailsPanel.this.getDisplayedObject())) {
          EMapDetailsPanel.this.refresh();
        }
      }
    });

    excludeTags.add("magStyle");
    excludeTags.add("regionicon");
  }

  private boolean isEditAll() {
    return settings.getProperty(PropertiesHelper.ORDEREDITOR_EDITALLFACTIONS,
        Boolean.FALSE.toString()).equals("true");
  }

  /**
   * Returns a panel with a Textfield for the name and a Textarea for the description.
   */
  public JPanel getNameAndDescriptionPanel() {
    return nameDescPanel;
  }

  /**
   * Returns the Details Tree panel for selected regions
   */
  public JPanel getDetailsPanel() {
    return pnlRegionInfoTree;
  }

  /**
   * Returns the multi order editor.
   */
  public JComponent getOrderEditor() {
    return editor;
  }

  /**
	 * 
	 */
  public NodeWrapperFactory getNodeWrapperFactory() {
    return nodeWrapperFactory;
  }

  private void init(GameData gameData) {
    data = gameData;
    gameSpecStuff = gameData.getGameSpecificStuff();
    gameRules = gameSpecStuff.getGameSpecificRules();
    orders.gameDataChanged(new GameDataEvent(this, data));
    unitsTools.setRules(gameData.rules);
    showNothing();
    // contextManager.setGameData(data);
  }

  /**
   * @see magellan.client.swing.InternationalizedDataPanel#gameDataChanged(magellan.library.event.GameDataEvent)
   */
  @Override
  public void gameDataChanged(GameDataEvent e) {
    init(e.getGameData());
  }

  protected GameSpecificStuff getGameSpecificStuff() {
    if (gameSpecStuff == null) {
      gameSpecStuff = data.rules.getGameSpecificStuff();
    }
    return gameSpecStuff;
  }

  protected GameSpecificRules getRules() {
    if (gameRules == null) {
      gameRules = getGameSpecificStuff().getGameSpecificRules();
    }
    return gameRules;
  }

  /*
   * Function to translate a\nb (view representation to "a\\nb" (String representation) It also
   * translates \r to \n
   */
  protected String normalizeDescription(String s) {
    if (s == null)
      return null;

    StringBuffer ret = new StringBuffer(s.length());

    for (StringTokenizer st = new StringTokenizer(s, "\n\r"); st.hasMoreTokens();) {
      ret.append(st.nextToken());

      if (st.hasMoreTokens()) {
        ret.append("\\n");
      }
    }

    return ret.toString();
  }

  private static final String DESCRIPTION_SEPARATOR = "\n---\n";

  protected String getDescriptionPart(String s) {
    if (s.indexOf(EMapDetailsPanel.DESCRIPTION_SEPARATOR) == -1)
      return normalizeDescription(s);

    return normalizeDescription(s.substring(0, s.indexOf(EMapDetailsPanel.DESCRIPTION_SEPARATOR)));
  }

  protected String getPrivatePart(String s) {
    if (s.indexOf(EMapDetailsPanel.DESCRIPTION_SEPARATOR) == -1)
      return "";

    return normalizeDescription(s.substring(s.indexOf(EMapDetailsPanel.DESCRIPTION_SEPARATOR)
        + EMapDetailsPanel.DESCRIPTION_SEPARATOR.length()));
  }

  /**
   * @deprecated Use {@link magellan.library.utils.Units#isPrivilegedAndNoSpy(Unit)} instead
   */
  @Deprecated
  public static boolean isPrivilegedAndNoSpy(Unit u) {
    return magellan.library.utils.Units.isPrivilegedAndNoSpy(u);
  }

  /**
   * @deprecated Use {@link magellan.library.utils.Units#isPrivileged(Faction)} instead
   */
  @Deprecated
  public static boolean isPrivileged(Faction f) {
    return magellan.library.utils.Units.isPrivileged(f);
  }

  private void setNameAndDescription(Described d, boolean isEditable) {
    if (d == null)
      return;

    setNameAndDescription(data.getTranslation(d), d.getDescription(), isEditable);
  }

  private void setNameAndDescription(String n, String desc, boolean isEditable) {
    // pavkovic 2003.10.07: dont notify this as undo action:
    // disable undo
    name.getDocument().removeUndoableEditListener(undoMgr);
    description.getDocument().removeUndoableEditListener(undoMgr);

    // change values
    name.setText((n == null) ? "" : n);
    name.setCaretPosition(0);
    name.setEditable(isEditable);
    description.setText((desc == null) ? "" : desc);
    description.setCaretPosition(0);
    description.setEditable(isEditable);

    // enable undo
    name.getDocument().addUndoableEditListener(undoMgr);
    description.getDocument().addUndoableEditListener(undoMgr);
  }

  private void showRegion(Region r) {
    // make editable for privileged units
    setNameAndDescription(r, (r != null)
        && (isEditAll() || magellan.library.utils.Units.isPrivilegedAndNoSpy(r
            .getModifiedOwnerUnit())));

    // build tree
    appendRegionInfo(r, rootNode, myExpandableNodes);

    // enable enable tag button (disable remove tag button?)
    addTag.setEnabled(true);
    removeTag.setEnabled(false);
  }

  /**
   * Shows a tree: Terrain : region.type Coordinates : region.coordinates guarding units (Orc
   * Infestination) (resources) (peasants) (luxuries) (schemes) (comments) (tags)
   * 
   * @param r
   * @param parent
   * @param expandableNodes
   */
  private void appendRegionInfo(Region r, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // terrain type
    parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.terrain") + ": "
        + r.getType().getName() + " ("
        + Resources.get("emapdetailspanel.node.terrain.visibility." + r.getVisibility().toString())
        + ")", r.getType().getID() + "-detail"));

    // terrain coordinates
    String regionKoordinateInfo =
        Resources.get("emapdetailspanel.node.coordinates") + ": " + r.getID();
    if (r.getUID() > 0) {
      regionKoordinateInfo +=
          " (ID:" + Integer.toString((int) r.getUID(), getGameData().base).replace("l", "L") + ")";
    }
    parent.add(createSimpleNode(regionKoordinateInfo, "koordinaten"));

    appendRegionOwnerInfo(r, parent, expandableNodes);

    // region guards
    appendRegionGuardInfo(r, parent, expandableNodes);

    // orc infestation
    if (r.isOrcInfested()) {
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.orcinfestation"),
          "orkinfest"));
    }

    // resources
    appendRegionResourceInfo(r, parent, expandableNodes);

    // peasants
    appendRegionPeasantInfo(r, parent, expandableNodes);

    // luxuries
    appendRegionLuxuriesInfo(r, parent, expandableNodes);

    // schemes
    appendRegionSchemes(r, parent, expandableNodes);

    // comments
    appendComments(r, parent, expandableNodes);

    // tags
    appendTags(r, parent, expandableNodes);
  }

  private void appendRegionOwnerInfo(Region r, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if (r.getOwnerUnit() != null) {
      UnitNodeWrapper w =
          nodeWrapperFactory.createUnitNodeWrapper(r.getOwnerUnit(), Resources
              .get("emapdetailspanel.node.owner")
              + ": ", r.getOwnerUnit().getPersons(), r.getOwnerUnit().getModifiedPersons());
      DefaultMutableTreeNode ownerNode = new DefaultMutableTreeNode(w);
      parent.add(ownerNode);
      // expandableNodes.add(w);

      Faction ownerFaction = r.getOwnerFaction();
      if (ownerFaction == null) {
        ownerFaction = r.getOwnerUnit().getFaction();
      }

      DefaultMutableTreeNode n;
      if (ownerFaction == null) {
        n =
            createSimpleNode(Resources.get("emapdetailspanel.node.faction") + ": "
                + Resources.get("emapdetailspanel.node.unknownfaction"), "faction");
      } else {
        n =
            createSimpleNode(Resources.get("emapdetailspanel.node.faction") + ": "
                + ownerFaction.toString(), "faction");
      }
      parent.add(n);
    }

    // E3 Morale
    if (r.getMorale() >= 0) {
      DefaultMutableTreeNode n;
      if (r.getMourning() == 1) {
        n =
            createSimpleNode(Resources.get("emapdetailspanel.node.morale.mourning",
                new Object[] { r.getMorale() }), "morale");
      } else {
        n =
            createSimpleNode(Resources.get("emapdetailspanel.node.morale", new Object[] { r
                .getMorale() }), "morale");
      }
      parent.add(n);
    }
  }

  /**
   * This function adds a node with subnodes to given parent - Luxuries: amount luxury item 1: price
   * 1 ... luxury item n: price n
   * 
   * @param r
   * @param parent
   * @param expandableNodes
   */
  private void appendRegionLuxuriesInfo(Region r, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if ((r == null) || (r.getPrices() == null) || r.getPrices().values().isEmpty())
      return;

    /*
     * DefaultMutableTreeNode luxuriesNode = new
     * DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.trade") + ": " +
     * getDiffString(r.maxLuxuries(), r.maxOldLuxuries()));
     */
    DefaultMutableTreeNode luxuriesNode =
        createSimpleNode(Resources.get("emapdetailspanel.node.trade") + ": "
            + getDiffString(r.maxLuxuries(), r.maxOldLuxuries()), "handeln");

    parent.add(luxuriesNode);
    expandableNodes.add(new NodeWrapper(luxuriesNode, "EMapDetailsPanel.RegionLuxuriesExpanded"));

    for (LuxuryPrice p : r.getPrices().values()) {
      int oldPrice = -1;

      if (r.getOldPrices() != null) {
        LuxuryPrice old = r.getOldPrices().get(p.getItemType().getID());

        if (old != null) {
          oldPrice = old.getPrice();
        }
      }

      luxuriesNode.add(createSimpleNode(p.getItemType().getName() + ": "
          + getDiffString(p.getPrice(), oldPrice), "items/" + p.getItemType().getIconName()));
    }
  }

  /**
   * This function adds a node with subnodes to given parent - Peasants: amount / max amount
   * recruit: recruit amount of recruit amount silver: silver surplus: surplus wage: entertain:
   * 
   * @param r
   * @param parent
   * @param expandableNodes
   */
  private void appendRegionPeasantInfo(Region r, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if (r.getPeasants() <= 0) {
      // return;
      // removed...information like unterhaltung und lohn
      // are still needed, also if peasants=0
      // Fiete 20080220
      // this was stupid, now all oceans have peasant info...
      // now focusing on non-ocean-regions
    }

    if (r.getRegionType().isOcean())
      // do not show RegionPeasantInfo on Oceans
      return;

    // peasants
    int maxWorkers = Utils.getIntValue(getRules().getMaxWorkers(r), 0);
    int workers = Math.min(maxWorkers, r.getPeasants());
    int surplus = (workers * r.getPeasantWage()) - (r.getPeasants() * getPeasantMaintenance(r));
    int oldWorkers = Math.min(maxWorkers, r.getOldPeasants());
    int oldSurplus = -1;

    if ((oldWorkers != -1) && (r.getOldWage() != -1) && (r.getOldPeasants() != -1)) {
      oldSurplus =
          (oldWorkers * (r.getOldWage() + 1)) - (r.getOldPeasants() * getPeasantMaintenance(r));
    }

    String peasantsInfo =
        getDiffString(r.getPeasants(), r.getOldPeasants(), r.getModifiedPeasants()).toString();
    peasantsInfo += (" / " + maxWorkers);

    DefaultMutableTreeNode peasantsNode =
        createSimpleNode(Resources.get("emapdetailspanel.node.peasants") + ": " + peasantsInfo,
            "bauern");

    parent.add(peasantsNode);
    expandableNodes.add(new NodeWrapper(peasantsNode, "EMapDetailsPanel.RegionPeasantsExpanded"));

    // recruit
    peasantsNode.add(createSimpleNode(Resources.get("emapdetailspanel.node.recruit") + ": "
        + getDiffString(r.maxRecruit(), r.maxRecruit(), r.modifiedRecruit()) + " "
        + Resources.get("emapdetailspanel.node.of") + " " + r.maxRecruit(), "rekruten"));

    // silver
    // Fiete 20080805: if we have silver in Resources, this would be redundant
    if (!isResourceTypeIDInRegionResources(r, rsilverID)) {
      peasantsNode.add(createSimpleNode(Resources.get("emapdetailspanel.node.silver") + ": "
          + getDiffString(r.getSilver(), r.getOldSilver()), "items/silber"));
    }
    // surplus
    peasantsNode.add(createSimpleNode(Resources.get("emapdetailspanel.node.surplus") + ": "
        + getDiffString(surplus, oldSurplus), "items/silber"));

    // wage
    int wage = r.getWage();

    if (wage != Integer.MIN_VALUE) {
      StringBuffer nodeText =
          new StringBuffer(Resources.get("emapdetailspanel.node.wage")).append(": ").append(
              getDiffString(r.getWage(), r.getOldWage()));

      for (Iterator<Race> it = data.rules.getRaceIterator(); it.hasNext();) {
        Race race = it.next();
        int rWage = data.getGameSpecificStuff().getGameSpecificRules().getWage(r, race);
        if (rWage > 0 && rWage != wage) {
          nodeText.append(", ").append(race.getName()).append(": ").append(rWage);
        }
      }
      peasantsNode.add(createSimpleNode(nodeText.toString(), "lohn"));
    }

    if (r.maxEntertain() != Integer.MIN_VALUE) {
      // entertain
      peasantsNode.add(createSimpleNode(Resources.get("emapdetailspanel.node.entertainment") + ": "
          + getDiffString(r.maxEntertain(), r.maxOldEntertain()), "items/silber"));
    }
  }

  private int getPeasantMaintenance(Region region) {
    return data.getGameSpecificStuff().getGameSpecificRules().getPeasantMaintenance(region);
  }

  /**
   * Appends the resources of the region.
   * 
   * @param r
   * @param parent
   * @param expandableNodes
   */
  private void appendRegionResourceInfo(Region r, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    DefaultMutableTreeNode resourceNode =
        createSimpleNode(Resources.get("emapdetailspanel.node.resources"), "ressourcen");
    String icon = null;
    if (!r.resources().isEmpty()) {
      // resources of region
      for (RegionResource res : r.resources()) {
        if (!res.getType().getID().equals(EresseaConstants.I_PEASANTS)) {
          int oldValue = findOldValueByResourceType(r, res);
          appendResource(res, resourceNode, oldValue);
        }
      }
    } else {
      // here we don't have resources, so make the "classic" way to create resource information
      if ((r.getTrees() > 0) || (r.getOldTrees() > 0)) {
        DefaultMutableTreeNode treeNode;

        if (r.isMallorn()) {
          icon = "items/" + rmallornID;
          if (getMagellanContext().getImageFactory().existImageIcon(icon + "_region")) {
            icon += "_region";
          }
          treeNode =
              createSimpleNode(Resources.get("emapdetailspanel.node.mallorntrees") + ": "
                  + getDiffString(r.getTrees(), r.getOldTrees()), icon);
        } else {
          icon = "items/" + rtreesID;
          if (getMagellanContext().getImageFactory().existImageIcon(icon + "_region")) {
            icon += "_region";
          }
          treeNode =
              createSimpleNode(Resources.get("emapdetailspanel.node.trees") + ": "
                  + getDiffString(r.getTrees(), r.getOldTrees()), icon);
        }

        resourceNode.add(treeNode);
      }

      if ((r.getIron() > 0) || (r.getOldIron() > 0)) {
        icon = "items/eisen";
        if (getMagellanContext().getImageFactory().existImageIcon(icon + "_region")) {
          icon += "_region";
        }
        resourceNode.add(createSimpleNode(data.rules.getItemType(EresseaConstants.I_RIRON)
            .getName()
            + ": " + getDiffString(r.getIron(), r.getOldIron()), icon));
      }

      if ((r.getLaen() > 0) || (r.getOldLaen() > 0)) {
        icon = "items/" + rlaenID;
        if (getMagellanContext().getImageFactory().existImageIcon(icon + "_region")) {
          icon += "_region";
        }
        resourceNode.add(createSimpleNode(Resources.get("emapdetailspanel.node.laen") + ": "
            + getDiffString(r.getLaen(), r.getOldLaen()), icon));
      }
    }

    // horse
    if ((r.getHorses() > 0) || (r.getOldHorses() > 0)) {
      // Fiete 20080805: only, if horses are not already included in ressources
      if (!isResourceTypeIDInRegionResources(r, rhorsesID)) {
        icon = "items/pferd";
        if (getMagellanContext().getImageFactory().existImageIcon(icon + "_region")) {
          icon += "_region";
        }
        resourceNode.add(createSimpleNode(Resources.get("emapdetailspanel.node.horses") + ": "
            + getDiffString(r.getHorses(), r.getOldHorses()), icon));
      }
    }

    // herb
    if (r.getHerb() != null) {
      StringBuffer sb =
          new StringBuffer(Resources.get("emapdetailspanel.node.herbs")).append(": ").append(
              r.getHerb().getName());

      if (r.getHerbAmount() != null) {
        sb.append(" (").append(r.getHerbAmount()).append(")");
      }

      icon = null;

      try {
        // bug 254..use the herb specified icon..at least try to find it
        // icon = r.getHerb().getMakeSkill().getSkillType().getID().toString();
        icon = "items/" + r.getHerb().getIconName();
      } catch (Exception exc) {
        icon = "kraeuterkunde";
      }

      resourceNode.add(createSimpleNode(sb.toString(), icon));
    }

    if (resourceNode.getChildCount() > 0) {
      parent.add(resourceNode);
      expandableNodes
          .add(new NodeWrapper(resourceNode, "EMapDetailsPanel.RegionResourcesExpanded"));
    }
  }

  private int findOldValueByResourceType(Region r, RegionResource res) {
    if ((r == null) || (res == null))
      return -1;

    if (res.getType().getID().equals(rironID))
      return r.getOldIron();

    if (res.getType().getID().equals(rlaenID))
      return r.getOldLaen();

    if (res.getType().getID().equals(rtreesID) || res.getType().getID().equals(rmallornID))
      return r.getOldTrees();

    if (res.getType().getID().equals(rsproutsID) || res.getType().getID().equals(rmallornSproutsID))
      return r.getOldSprouts();

    if (res.getType().getID().equals(rstonesID))
      return r.getOldStones();

    if (res.getType().getID().equals(rhorsesID))
      return r.getOldHorses();

    if (res.getType().getID().equals(rsilverID))
      return r.getOldSilver();

    if (res.getType().getID().equals(rpeasantsID))
      return r.getOldPeasants();

    return -1;
  }

  private boolean isResourceTypeIDInRegionResources(Region r, StringID resourceID) {
    if (r.resources() == null || r.resources().isEmpty())
      return false;

    for (RegionResource res : r.resources()) {
      if (res.getType().getID().equals(resourceID)) {
        if (r.getResource(data.rules.getItemType(resourceID)) == null) {
          resourceID = null;
        }
        return true;
      }
    }

    if (r.getResource(data.rules.getItemType(resourceID)) != null) {
      resourceID = null;
    }
    return false;
  }

  private void appendMultipleRegionInfo(Collection<Region> r, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // collect the data
    // Sort regions for types. Key: RegionType; Value: LinkedList containing the region-objects
    Map<UnitContainerType, List<Region>> regions = new Hashtable<UnitContainerType, List<Region>>();

    // Collect resources: key: ItemType; value: Integer
    Map<ItemType, Integer> resources = new Hashtable<ItemType, Integer>();

    // Collect herbs: key: ItemType; value: LinkedList containing the region-objects
    Map<ItemType, List<Region>> herbs = new Hashtable<ItemType, List<Region>>();

    // Count peasants, silver
    int peasants = 0;
    int silver = 0;

    for (Region region : r) {
      if (region.getPeasants() != -1) {
        peasants += region.getPeasants();
      }

      if (region.getSilver() != -1) {
        silver += region.getSilver();
      }

      if (region.getHerb() != null) {
        List<Region> regionList = herbs.get(region.getHerb());

        if (regionList == null) {
          regionList = new LinkedList<Region>();
          herbs.put(region.getHerb(), regionList);
        }

        regionList.add(region);
      }

      List<Region> list = regions.get(region.getType());

      if (list == null) {
        list = new LinkedList<Region>();
        regions.put(region.getType(), list);
      }

      list.add(region);

      if (!region.resources().isEmpty()) {
        for (RegionResource res : region.resources()) {
          Integer amount = resources.get(res.getType());
          int i = res.getAmount();

          if (amount != null) {
            i += amount.intValue();
          }

          resources.put(res.getType(), new Integer(i));
        }
      }

      if (region.getHorses() > 0) {
        ItemType iType = data.rules.getItemType(EresseaConstants.I_RHORSES);
        Integer amount = resources.get(iType);
        int i = region.getHorses();

        if (amount != null) {
          i += amount.intValue();
        }

        resources.put(iType, new Integer(i));
      }
    }

    // Now the data is prepared. Build the tree:
    // peasants
    parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.peasants") + ": " + peasants,
        "bauern"));

    // silver
    parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.silver") + ": " + silver,
        "items/silber"));

    // terrains sorted by region type
    DefaultMutableTreeNode terrainsNode =
        new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.terrains"));
    parent.add(terrainsNode);
    expandableNodes.add(new NodeWrapper(terrainsNode, "EMapDetailsPanel.RegionTerrainsExpanded"));

    List<UnitContainerType> sortedList1 = new LinkedList<UnitContainerType>(regions.keySet());
    Collections.sort(sortedList1, new NameComparator(IDComparator.DEFAULT));

    for (ListIterator<UnitContainerType> iter = sortedList1.listIterator(); iter.hasNext();) {
      UnitContainerType rType = iter.next();
      List<Region> list = regions.get(rType);
      Collections.sort(list, new NameComparator(IDComparator.DEFAULT));

      DefaultMutableTreeNode regionsNode =
          createSimpleNode(rType.getName() + ": " + list.size(), rType.getID().toString());
      terrainsNode.add(regionsNode);

      for (Region region : list) {
        int persons = 0;

        for (Unit unit : region.units()) {
          persons += unit.getPersons();
        }

        regionsNode.add(new DefaultMutableTreeNode(nodeWrapperFactory.createRegionNodeWrapper(
            region, persons)));
      }
    }

    // resources of the regions sorted by name,id of resource
    DefaultMutableTreeNode resourcesNode =
        new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.resources"));
    List<ItemType> sortedList2 = new LinkedList<ItemType>(resources.keySet());
    Collections.sort(sortedList2, new NameComparator(IDComparator.DEFAULT));

    for (ListIterator<ItemType> iter = sortedList2.listIterator(); iter.hasNext();) {
      ItemType resType = iter.next();
      int amount = resources.get(resType);

      if (amount > 0) {
        resourcesNode.add(createSimpleNode(resType.getName() + ": " + amount, "items/"
            + resType.getIconName()));
      }
    }

    if (resourcesNode.getChildCount() > 0) {
      parent.add(resourcesNode);
      expandableNodes
          .add(new NodeWrapper(resourcesNode, "EMapDetailsPanel.RegionResourcesExpanded"));
    }

    // herbs of the regions sorted by name, id of herb
    DefaultMutableTreeNode herbsNode =
        new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.herbs"));
    List<ItemType> sortedList3 = new LinkedList<ItemType>(herbs.keySet());
    Collections.sort(sortedList3, new NameComparator(IDComparator.DEFAULT));

    for (ListIterator<ItemType> iter = sortedList3.listIterator(); iter.hasNext();) {
      ItemType herbType = iter.next();
      List<Region> regionList = herbs.get(herbType);
      int i = regionList.size();
      DefaultMutableTreeNode regionsNode =
          new DefaultMutableTreeNode(herbType.getName() + ": " + i, true);

      // m = new
      // DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(herbType.getName() + ": "
      // + i, herbType.getIconName()));
      herbsNode.add(regionsNode);
      Collections.sort(regionList, new NameComparator(IDComparator.DEFAULT));

      for (ListIterator<Region> iter2 = regionList.listIterator(); iter2.hasNext();) {
        Region myRegion = iter2.next();
        regionsNode.add(new DefaultMutableTreeNode(nodeWrapperFactory
            .createRegionNodeWrapper(myRegion)));
      }
    }

    if (herbsNode.getChildCount() > 0) {
      parent.add(herbsNode);
      expandableNodes.add(new NodeWrapper(herbsNode, "EMapDetailsPanel.HerbStatisticExpanded"));
    }
  }

  /**
   * Returns a simple node
   */
  private DefaultMutableTreeNode createSimpleNode(Object obj, String icons) {
    return new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(obj, icons));
  }

  private DefaultMutableTreeNode createSimpleNode(Named obj, String icons) {
    return new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(obj, data
        .getTranslation(obj), icons));
  }

  private DefaultMutableTreeNode createSimpleNode(Object obj, ArrayList<String> icons) {
    return new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(obj, icons));
  }

  /**
   * Return a string showing the signed difference of the two int values in the form "
   * <tt>current</tt> [&lt;sign&gt;&lt;number&gt;]". If the two values are equal or if one of them
   * is -1, an empty string is returned.
   */
  private StringBuffer getDiffString(int current, int old) {
    return getDiffString(current, old, current);
  }

  /**
   * Return a string showing the signed difference of the three int values in the form "
   * <tt>current</tt> (future) [&lt;sign&gt;&lt;change&gt;]". e.g. (1,2,1) returns 1 [-1] e.g.
   * (1,2,2) returns 1 (2) [-1] e.g. (1,1,2) returns 1 (2) e.g. (1,1,1) returns 1 If future == -1,
   * then ignore
   */
  private StringBuffer getDiffString(int current, int old, int future) {
    StringBuffer ret = new StringBuffer(String.valueOf(current));

    if (current == -1)
      return ret;

    if ((old != -1) && (current != old)) {
      ret.append(" ");
      ret.append(getDiffString(current - old, true));
    }

    // future value is treated differently, also negative numbers may exist
    if (current != future) {
      ret.append(" ");
      ret.append(getDiffString(future, false));
    }

    return ret;
  }

  private StringBuffer getDiffString(int value, boolean printSign) {
    StringBuffer ret = new StringBuffer(4);

    if (printSign) {
      ret.append("[");

      if (value > 0) {
        ret.append("+");
      }

      ret.append(value);
      ret.append("]");
    } else {
      ret.append("(");
      ret.append(value);
      ret.append(")");
    }

    return ret;
  }

  /**
   * Appends a folder with the (region) resources to the specified parent node.
   * 
   * @param r
   * @param parent
   * @param expandableNodes
   */
  private void appendRegionGuardInfo(Region r, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    Set<Faction> parties = new HashSet<Faction>();
    Set<Unit> units = new HashSet<Unit>();

    for (Unit unit : r.units()) {
      if (unit.getGuard() != 0) {
        parties.add(unit.getFaction());
        units.add(unit);
      }
    }

    if (parties.size() > 0) {
      // DefaultMutableTreeNode guardRoot = new
      // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.guarded") + ": ");
      // Fiete 20060911: added support for "bewacht" - icon
      DefaultMutableTreeNode guardRoot =
          new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
              .get("emapdetailspanel.node.guarded")
              + ": ", "bewacht"));

      parent.add(guardRoot);
      expandableNodes.add(new NodeWrapper(guardRoot, "EMapDetailsPanel.RegionGuardExpanded"));

      for (Faction faction : parties) {
        int iGuardFlags = 0;
        StringBuffer strFaction = new StringBuffer();
        DefaultMutableTreeNode guardParty = new DefaultMutableTreeNode(strFaction);
        guardRoot.add(guardParty);

        Iterator<Unit> iterUnits = units.iterator();

        while (iterUnits.hasNext()) {
          Unit unit = iterUnits.next();

          if (unit.getFaction().equals(faction)) {
            DefaultMutableTreeNode guardUnit =
                new DefaultMutableTreeNode(nodeWrapperFactory.createUnitNodeWrapper(unit));
            guardParty.add(guardUnit);
            iGuardFlags |= unit.getGuard();
          }
        }

        strFaction.append(faction.toString() + ", ");
        strFaction.append(MagellanFactory.guardFlagsToString(iGuardFlags));
      }
    }
  }

  /**
   * Appends a folder with the (region) resources to the specified parent node.
   * 
   * @param res
   * @param parent
   * @param oldValue
   */
  private void appendResource(RegionResource res, DefaultMutableTreeNode parent, int oldValue) {
    StringBuffer sb = new StringBuffer();
    sb.append(res.getType().getName());

    if ((res.getAmount() != -1) || (res.getSkillLevel() != -1)) {
      sb.append(": ");

      if (res.getAmount() != -1) {
        sb.append(getDiffString(res.getAmount(), oldValue));

        if (res.getSkillLevel() != -1) {
          sb.append(" / ");
        }
      }

      if (res.getSkillLevel() != -1) {
        sb.append("T").append(res.getSkillLevel());
      }
    }

    // Icon bauen
    String icon = res.getType().getIconName();

    if (icon.equalsIgnoreCase("Steine")) {
      icon = "stein";
    }

    if (getMagellanContext().getImageFactory().existImageIcon("items/" + icon + "_region")) {
      icon = icon + "_region";
    }

    icon = "items/" + icon;

    // new: make List of icons
    ArrayList<String> icons = new ArrayList<String>();
    icons.add(icon);

    // Aktualit�tsinfo

    if (res.getDate() != null && res.getDate().getDate() > -1) {
      if (res.getDate().equals(data.getDate())) {
        // same turn
        sb.append(" (" + Resources.get("emapdetailspanel.node.resinfo_current") + ") ");
        // icons.add("current");
      } else {
        int anzahlRunden = data.getDate().getDate() - res.getDate().getDate();
        String helperS;
        if (anzahlRunden <= 1) {
          helperS = Resources.get("emapdetailspanel.node.resinfo_old_one", anzahlRunden);
        } else {
          helperS = Resources.get("emapdetailspanel.node.resinfo_old_more", anzahlRunden);
        }
        sb.append(" (" + helperS + ") ");
        icons.add("outdated");
      }
    } else {
      sb.append(" (" + Resources.get("emapdetailspanel.node.resinfo_old_unknown") + ") ");
      icons.add("outdated");
    }

    parent.add(createSimpleNode(sb.toString(), icons));
  }

  /**
   * Appends a folder with the schemes in the specified region to the specified parent node.
   * 
   * @param region
   * @param parent
   * @param expandableNodes
   */
  private void appendRegionSchemes(Region region, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if (region.schemes().isEmpty())
      return;

    DefaultMutableTreeNode schemeNode =
        new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.schemes"));
    parent.add(schemeNode);
    expandableNodes.add(new NodeWrapper(schemeNode, "EMapDetailsPanel.RegionSchemesExpanded"));

    for (Scheme s : region.schemes()) {
      schemeNode.add(new DefaultMutableTreeNode(s));
    }
  }

  // NOTE: Don't know if to add the exclusion code for
  // well-known external tags (magStyle, regionicon)

  /**
   * Appends a folder with the external tags of a unit container to the given node.
   * 
   * @param uc
   * @param parent
   * @param expandableNodes
   */
  private void appendTags(UnitContainer uc, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    appendTags(uc, parent, expandableNodes, uc.getType().getID().toString() + "TagsExpanded");
  }

  /**
   * Appends a folder with the external tags of a unit to the given node.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendTags(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    appendTags(u, parent, expandableNodes, "UnitTagsExpanded");
  }

  /**
   * Appends the tags of a taggable.
   * 
   * @param taggable
   * @param parent
   * @param expandableNodes
   * @param nodeInfo An identifier for the expandable node
   */
  private void appendTags(Taggable taggable, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes, String nodeInfo) {
    if (!taggable.hasTags())
      return;

    DefaultMutableTreeNode tags =
        new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.tags"));

    for (String tempName : taggable.getTagMap().keySet()) {
      String value = taggable.getTag(tempName);
      tags.add(new DefaultMutableTreeNode(tempName + ": " + value));
    }

    parent.add(tags);
    expandableNodes.add(new NodeWrapper(tags, "EMapDetailsPanel." + nodeInfo));
  }

  private void showFaction(Faction f, List<?> context) {
    setNameAndDescription(f, magellan.library.utils.Units.isPrivileged(f));
    appendFactionsInfo(Collections.singletonList(f), Collections.<List<?>> singletonList(context),
        rootNode, myExpandableNodes);
  }

  /**
   * Append information for several selected factions.
   * 
   * @param factions
   * @param parent
   * @param expandableNodes
   */
  private void appendFactionsInfo(List<Faction> factions, List<? extends List<?>> contexts,
      DefaultMutableTreeNode parent, Collection<NodeWrapper> expandableNodes) {
    if (factions == null)
      return;

    // if only one faction selected...show faction info
    if (factions.size() == 1) {
      Faction f = factions.iterator().next();
      appendFactionInfo(f, f.getAllies(), f.getAlliance(), parent, expandableNodes);
    }

    Collection<Unit> units = new LinkedList<Unit>();
    Iterator<Faction> fIt = factions.iterator();
    Iterator<? extends List<?>> cIt = contexts.iterator();
    for (; fIt.hasNext();) {
      addFiltered(units, fIt.next().units(), cIt.next());
    }

    appendUnitsInfo(units, parent, expandableNodes);

    // comments
    for (Faction faction : factions) {
      appendComments(faction, parent, expandableNodes);
    }
  }

  /**
   * Filters units for being in all of the regions in context.
   * 
   * @param result
   * @param input
   * @param context
   */
  protected void addFiltered(Collection<Unit> result, Collection<Unit> input, List<?> context) {
    for (Unit u : input) {
      boolean include = true;
      for (Object o : context) {
        if (o instanceof Region) {
          include &= u.getRegion().equals(o);
        }
      }
      if (include) {
        result.add(u);
      }
    }
  }

  /**
   * Append summary information for several selected units.
   * 
   * @param units
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitsInfo(Collection<Unit> units, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {

    String text = "";

    Map<String, SkillStatItem> skills = new Hashtable<String, SkillStatItem>();

    // key: racename (string), Value: Integer-Object containing number of persons of that race
    // Fiete: Value: raceInfo with realRace ( nor Prefix, and the amount of Persons
    Map<String, RaceInfo> races = new Hashtable<String, RaceInfo>();

    // persons of all races
    RaceInfo allInfo = new RaceInfo();
    allInfo.raceNoPrefix = "";

    float uWeight = 0;
    float modUWeight = 0;

    // count persons of different races, weight and modified weight and skills within this loop
    for (Unit u : units) {
      // weight (Fiete)
      float actUWeight = getGameSpecificStuff().getMovementEvaluator().getWeight(u) / 100.0F;
      uWeight += actUWeight;

      float actModUWeight =
          getGameSpecificStuff().getMovementEvaluator().getModifiedWeight(u) / 100.0F;
      modUWeight += actModUWeight;

      // persons
      RaceInfo rInfo = races.get(u.getRaceName(data));
      if (rInfo == null) {
        rInfo = new RaceInfo();
        // rInfo.raceNoPrefix = com.eressea.util.Umlaut.convertUmlauts(u.getRealRaceName());
        // umlaut check is done late when loading the image
        rInfo.raceNoPrefix = u.getSimpleRealRaceName();
      }

      rInfo.amount += u.getPersons();
      rInfo.amount_modified += u.getModifiedPersons();
      allInfo.amount += u.getPersons();
      allInfo.amount_modified += u.getModifiedPersons();

      races.put(u.getRaceName(data), rInfo);

      // skills
      for (Skill skill : u.getSkills()) {
        SkillStatItem stored = skills.get(skill.getName() + skill.getLevel());

        if (stored != null) {
          stored.unitCounter += u.getPersons();
          stored.units.add(u);
        } else {
          stored = new SkillStatItem(skill, u.getPersons());
          stored.units.add(u);
          skills.put(skill.getName() + skill.getLevel(), stored);
        }
      }
    }
    DefaultMutableTreeNode raceparent = parent;
    if (races.size() > 1) {
      // show xyz Personen and later add race nodes to person node
      DefaultMutableTreeNode personNode;
      if (allInfo.amount == allInfo.amount_modified) {
        personNode =
            createSimpleNode(allInfo.amount + " " + Resources.get("emapdetailspanel.node.persons")
                + ":", "person");
      } else {
        personNode =
            createSimpleNode(allInfo.amount + " (" + allInfo.amount_modified + ") "
                + Resources.get("emapdetailspanel.node.persons") + ":", "person");
      }
      parent.add(personNode);
      raceparent = personNode;
      expandableNodes.add(new NodeWrapper(raceparent, "EMapDetailsPanel.persons.Expanded"));
    }
    // show abc Dwarfs\n 123 Zwerge...
    for (Entry<String, RaceInfo> e : races.entrySet()) {
      String race = e.getKey();
      RaceInfo rI = e.getValue();
      int i = rI.amount;
      int i_modified = rI.amount_modified;
      String personIconName = "person";
      // we check if specific icon for race exists, if so, we use it
      // Fiete 20061218
      if (getMagellanContext().getImageFactory().existImageIcon(rI.raceNoPrefix)) {
        personIconName = rI.raceNoPrefix;
      }
      if (i_modified == i) {
        raceparent.add(createSimpleNode(i + " " + race, personIconName));
      } else {
        raceparent.add(createSimpleNode(i + " (" + i_modified + ") " + race, personIconName));
      }
    }

    // show weight (Fiete)
    text =
        Resources.get("emapdetailspanel.node.totalweight") + ": "
            + EMapDetailsPanel.weightNumberFormat.format(uWeight);

    if (uWeight != modUWeight) {
      text += (" (" + EMapDetailsPanel.weightNumberFormat.format(modUWeight) + ")");
    }

    text += (" " + Resources.get("emapdetailspanel.node.weightunits"));
    parent.add(createSimpleNode(text, "gewicht"));

    // categorized items
    Collection<TreeNode> catNodes =
        unitsTools.addCategorizedUnitItems(units, parent, null, null, true, nodeWrapperFactory,
            null);
    if (catNodes != null) {
      for (TreeNode treeNode : catNodes) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode;
        Object o = node.getUserObject();
        ItemCategory cat = null;

        if (o instanceof ItemCategoryNodeWrapper) {
          cat = ((ItemCategoryNodeWrapper) o).getItemCategory();
        } else {
          cat = (ItemCategory) o;
        }

        expandableNodes.add(new NodeWrapper(node, "EMapDetailsPanel." + cat.getID().toString()
            + "Expanded"));
      }
    }

    // show skills
    if (skills.size() > 0) {
      // DefaultMutableTreeNode skillsNode = new
      // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.skills"));
      DefaultMutableTreeNode skillsNode =
          new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
              .get("emapdetailspanel.node.skills"), "skills"));
      parent.add(skillsNode);
      expandableNodes.add(new NodeWrapper(skillsNode, "EMapDetailsPanel.FactionSkillsExpanded"));

      List<SkillStatItem> sortedSkills = new LinkedList<SkillStatItem>(skills.values());
      Collections.sort(sortedSkills, new SkillStatItemComparator());

      for (SkillStatItem item : sortedSkills) {
        DefaultMutableTreeNode skillNode =
            createSimpleNode(item.skill.getName() + " " + item.skill.getLevel() + ": "
                + item.unitCounter, item.skill.getSkillType().getID().toString());
        skillsNode.add(skillNode);

        Comparator<Unique> idCmp = IDComparator.DEFAULT;
        Comparator<Unit> unitCmp =
            new UnitSkillComparator(new SpecifiedSkillTypeSkillComparator(
                item.skill.getSkillType(), new SkillComparator(), null), idCmp);
        Collections.sort(item.units, unitCmp);

        StringBuilder sb = new StringBuilder();
        for (Unit u : item.units) {
          sb.delete(0, sb.length());
          sb.append(u.toString());

          Skill skill = u.getSkill(item.skill.getSkillType());

          if (skill != null) {
            int bonus = 0;
            if (u.getRace() != null && u.getRegion() != null) {
              bonus = u.getRace().getSkillBonus(skill.getSkillType());
              bonus +=
                  u.getRace().getSkillBonus(item.skill.getSkillType(),
                      u.getRegion().getRegionType());
            }

            sb.append(": [").append(skill.getPointsPerPerson()).append(" -> ").append(
                Skill.getPointsAtLevel((item.skill.getLevel() - bonus) + 1)).append("]");
          }

          sb.append(", " + u.getPersons());

          if (u.getPersons() != u.getModifiedPersons()) {
            sb.append(" (").append(u.getModifiedPersons()).append(")");
          }

          skillNode.add(new DefaultMutableTreeNode(nodeWrapperFactory.createUnitNodeWrapper(u, sb
              .toString())));
        }
      }
    }
  }

  private void showGroup(Group g, List<?> context) {
    setNameAndDescription(g.getName(), "", false);
    appendGroupInfo(g, context, rootNode, myExpandableNodes);
  }

  /**
   * Append information about the specified group.
   * 
   * @param g
   * @param parent
   * @param expandableNodes
   */
  private void appendGroupInfo(Group g, List<?> context, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    Map<String, SkillStatItem> skills = new Hashtable<String, SkillStatItem>();
    Collection<Unit> groupUnits = new LinkedList<Unit>();
    addFiltered(groupUnits, g.getFaction().units(), context);
    int personCount = gatherGroupInfo(g, groupUnits, skills);
    appendGroupPersons(g, personCount, parent, expandableNodes);
    appendGroupAlliances(g, parent, expandableNodes);
    appendGroupItems(g, groupUnits, parent, expandableNodes);
    appendGroupSkills(g, skills, parent, expandableNodes);
  }

  private int gatherGroupInfo(Group g, Collection<Unit> groupUnits,
      Map<String, SkillStatItem> skills) {
    int personCount = 0;

    for (Unit u : groupUnits) {

      if ((u.getGroup() != null) && u.getGroup().equals(g)) {
        personCount += u.getPersons();
        groupUnits.add(u);

        for (Skill skill : u.getSkills()) {
          SkillStatItem stored = skills.get(skill.getName() + skill.getLevel());

          if (stored != null) {
            stored.unitCounter += u.getPersons();
            stored.units.add(u);
          } else {
            stored = new SkillStatItem(skill, u.getPersons());
            stored.units.add(u);
            skills.put(skill.getName() + skill.getLevel(), stored);
          }
        }
      }
    }
    return personCount;
  }

  private void appendGroupPersons(Group g, int personCount, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    DefaultMutableTreeNode n =
        new DefaultMutableTreeNode(personCount + " "
            + Resources.get("emapdetailspanel.node.persons"));
    parent.add(n);
  }

  private void appendGroupAlliances(Group g, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // ALLIES
    if ((g.allies() != null) && (g.allies().values().size() > 0)) {
      DefaultMutableTreeNode n =
          new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.alliances"));
      parent.add(n);
      expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.AlliancesExpanded"));

      FactionStatsPanel.showAlliances(data, g.allies(), g.getFaction().getAlliance(), n);
    }
  }

  private void appendGroupItems(Group g, Collection<Unit> regionUnits,
      DefaultMutableTreeNode parent, Collection<NodeWrapper> expandableNodes) {
    // categorized items
    Collection<TreeNode> catNodes =
        unitsTools.addCategorizedUnitItems(regionUnits, parent, null, null, true,
            nodeWrapperFactory, null);
    if (catNodes != null) {
      for (TreeNode treeNode : catNodes) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode;

        if (EMapDetailsPanel.log.isDebugEnabled()) {
          EMapDetailsPanel.log.debug("EmapDetailPanel.appendGroupInfo: found class "
              + node.getUserObject().getClass() + " (expected ItemCategory)");
        }

        Object o = node.getUserObject();
        ItemCategory cat = null;

        if (o instanceof ItemCategoryNodeWrapper) {
          cat = ((ItemCategoryNodeWrapper) o).getItemCategory();
        } else {
          cat = (ItemCategory) o;
        }

        expandableNodes.add(new NodeWrapper(node, "EMapDetailsPanel." + cat.getID().toString()
            + "Expanded"));
      }
    }
  }

  private void appendGroupSkills(Group g, Map<String, SkillStatItem> skills,
      DefaultMutableTreeNode parent, Collection<NodeWrapper> expandableNodes) {
    // SKILLS
    if (skills.size() > 0) {
      // n = new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.skills"));
      DefaultMutableTreeNode n =
          new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
              .get("emapdetailspanel.node.skills"), "skills"));
      parent.add(n);
      expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.RegionSkillsExpanded"));

      List<SkillStatItem> sortedSkills = new LinkedList<SkillStatItem>(skills.values());
      Collections.sort(sortedSkills, new SkillStatItemComparator());

      for (SkillStatItem item : sortedSkills) {
        DefaultMutableTreeNode m =
            new DefaultMutableTreeNode(new SimpleNodeWrapper(item.skill.getName() + " "
                + item.skill.getLevel() + ": " + item.unitCounter, item.skill.getSkillType()
                .getID().toString()));
        n.add(m);

        Comparator<Unique> idCmp = IDComparator.DEFAULT;
        ToStringComparator<Skill> nameComparator = new ToStringComparator<Skill>(null);
        SkillRankComparator skillRankComparator = new SkillRankComparator(nameComparator, settings);
        // SkillTypeComparator<Skill> skillTypeComparator = new
        // SkillTypeComparator<Skill>(skillRankComparator, null);
        BestSkillComparator bestSkillComparator =
            new BestSkillComparator(SkillComparator.skillCmp, skillRankComparator,
                SkillComparator.skillCmp);
        Comparator<Unit> unitCmp = new UnitSkillComparator(bestSkillComparator, idCmp);
        /*
         * Comparator<Unit> unitCmp = new UnitSkillComparator(new BestSkillComparator<SkillType>(new
         * SkillComparator(), new SkillTypeComparator(new SkillTypeRankComparator<Named>(new
         * NameComparator<Unique>(idCmp),settings), new SkillComparator()), null), idCmp);
         */
        Collections.sort(item.units, unitCmp);

        for (Unit u : item.units) {
          StringBuilder sb = new StringBuilder();
          sb.append(u.toString());

          Skill skill = u.getSkill(item.skill.getSkillType());

          if (skill != null) {
            int bonus = 0;
            if (u.getRace() != null && u.getRegion() != null) {
              bonus = u.getRace().getSkillBonus(skill.getSkillType());
              bonus +=
                  u.getRace().getSkillBonus(item.skill.getSkillType(),
                      u.getRegion().getRegionType());
            }

            sb.append(": [").append(skill.getPointsPerPerson()).append(" -> ").append(
                Skill.getPointsAtLevel((item.skill.getLevel() - bonus) + 1)).append("]");
          }

          sb.append(", ").append(u.getPersons());

          if (u.getPersons() != u.getModifiedPersons()) {
            sb.append(" (").append(u.getModifiedPersons()).append(")");
          }

          UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u, sb.toString());
          m.add(new DefaultMutableTreeNode(w));
        }
      }
    }
  }

  private void showUnit(Unit u) {
    // update description text area
    String strDesc = "";

    if (u.getDescription() != null) {
      strDesc += u.getDescription();
    }

    if (u.getPrivDesc() != null) {
      strDesc += (EMapDetailsPanel.DESCRIPTION_SEPARATOR + u.getPrivDesc());
    }

    setNameAndDescription(u.getName(), strDesc, isEditAll()
        || magellan.library.utils.Units.isPrivilegedAndNoSpy(u));

    appendUnitInfo(u, rootNode, myExpandableNodes);

    addTag.setEnabled(true);
    removeTag.setEnabled(false);
  }

  /**
   * Appends information for the specified unit
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    /* make sure that all unit relations have been established */
    if (u.getRegion() != null) {
      u.getRegion().refreshUnitRelations();
    }

    // Heldenanzeige
    if (u.isHero()) {
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.hero"), "hero"));
    }

    // Personenanzeige
    appendUnitPersonInfo(u, parent, expandableNodes);

    // Partei
    appendUnitFactionInfo(u, parent, expandableNodes);

    // Race
    appendUnitRaceInfo(u, parent, expandableNodes);

    // Group
    appendUnitGroupInfo(u, parent, expandableNodes);

    // Kampfreihenanzeige
    SimpleNodeWrapper cWrapper =
        nodeWrapperFactory.createSimpleNodeWrapper(Resources
            .get("emapdetailspanel.node.combatstatus")
            + ": " + MagellanFactory.combatStatusToString(u), "kampfstatus");

    if (magellan.library.utils.Units.isPrivilegedAndNoSpy(u)) {
      cWrapper.setContextFactory(combatContext);
      cWrapper.setArgument(u);
    }
    parent.add(new DefaultMutableTreeNode(cWrapper));

    // starvation
    if (u.isStarving()) {
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.starved"), "hunger"));
    }

    // health state
    if (u.getHealth() != null) {
      // Fiete 20060910
      // the hp-string is not translated in the cr
      // so u.health = german
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.health") + ": "
          + data.getTranslation(u.getHealth()), u.getHealth()));
    }

    // guard state
    if (u.getGuard() != 0 || (u.getGuard() != u.getModifiedGuard())) {
      String text = "";
      if (u.getGuard() != 0) {
        text =
            Resources.get("emapdetailspanel.node.guards") + ": "
                + MagellanFactory.guardFlagsToString(u.getGuard());
      } else {
        text = Resources.get("emapdetailspanel.node.guardsnot");
      }
      if (u.getGuard() != u.getModifiedGuard()) {
        text += " (";
        if (u.getModifiedGuard() != 0) {
          text +=
              Resources.get("emapdetailspanel.node.guards") + ": "
                  + MagellanFactory.guardFlagsToString(u.getModifiedGuard());
        } else {
          text += Resources.get("emapdetailspanel.node.guardsnot");
        }
        text += ")";
      }
      parent.add(createSimpleNode(text, "bewacht"));
    }

    // stealth info
    appendUnitStealthInfo(u, parent, expandableNodes);

    // spy
    if (u.isSpy()) {
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.spy"), "spy"));
    }

    // Gebaeude-/Schiffsanzeige
    appendContainerInfo(u, parent, expandableNodes);

    // magic aura
    if (u.getAura() != -1) {
      String spellSchool = "";
      if (u.getFaction() != null && u.getFaction().getSpellSchool() != null) {
        spellSchool = " (" + u.getFaction().getSpellSchool() + ")";
      }
      String auraInfo =
          Resources.get("emapdetailspanel.node.aura") + ": " + u.getAura() + " / " + u.getAuraMax();
      if (spellSchool.length() > 1) {
        auraInfo += spellSchool;
      }
      parent.add(createSimpleNode(auraInfo, "aura"));
    }

    // familiar
    appendFamiliarInfo(u, parent, expandableNodes);

    // weight
    appendUnitWeight(u, parent, expandableNodes);

    // Fiete 20060915: feature wish..calculate max Horses for walking and riding
    appendUnitHorses(u, parent, expandableNodes);

    // load
    appendUnitLoadInfo(u, parent, expandableNodes);

    // items
    appendUnitItemInfo(u, parent, expandableNodes);

    // skills
    boolean isTrader = appendUnitSkillInfo(u, parent, expandableNodes);

    // teaching
    appendUnitTeachInfo(u, parent, expandableNodes);

    // transportation
    appendUnitTransportationInfo(u, parent, expandableNodes);

    // attacking
    appendUnitAttackInfo(u, parent, expandableNodes);

    // magic spells
    appendUnitSpellsInfo(u, parent, expandableNodes);

    // Kampfzauber-Anzeige
    appendUnitCombatSpells(u.getCombatSpells(), parent, expandableNodes);

    // Trank Anzeige
    appendUnitPotionInfo(u, parent, expandableNodes);

    // luxuries
    if (isTrader) {
      appendRegionLuxuriesInfo(u.getRegion(), parent, expandableNodes);
    }

    // region resources if maker
    appendUnitRegionResource(u, parent, expandableNodes);

    // comments
    appendComments(u, parent, expandableNodes);

    // tags
    appendTags(u, parent, expandableNodes);
  }

  /**
   * Appends information on number of persons (and their race).
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitPersonInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {

    // display custom Unit Icon ?
    String customUnitIconFileName = "custom/units/" + u.toString(false);
    if (isAllowingCustomIcons()
        && getMagellanContext().getImageFactory().existImageIcon(customUnitIconFileName)) {
      DefaultMutableTreeNode customUnitIconNode = null;
      if (getMagellanContext().getImageFactory().imageIconSizeCheck(customUnitIconFileName, 40, 40)) {
        customUnitIconNode = createSimpleNode(u.getModifiedName(), customUnitIconFileName);
      } else {
        customUnitIconNode = createSimpleNode(u.getModifiedName(), "toobig");
      }
      parent.add(customUnitIconNode);
    }

    String strPersons = Resources.get("emapdetailspanel.node.persons") + ": " + u.getPersons();

    if (u.getPersons() != u.getModifiedPersons()) {
      strPersons += (" (" + u.getModifiedPersons() + ")");
    }
    String iconPersonName = "person";
    /**
     * Fiete 20061218...was just a first try....not that good, needs allways some support.. String
     * iconNameEN = getString(com.eressea.util.Umlaut.convertUmlauts(u.getRealRaceName())); if
     * (!iconNameEN.equalsIgnoreCase(com.eressea.util.Umlaut.convertUmlauts(u.getRealRaceName()))) {
     * iconPersonName = iconNameEN; }
     */
    // now we check if a specific race icon exists, if true, we use it
    if (getMagellanContext().getImageFactory().existImageIcon(u.getSimpleRealRaceName())) {
      iconPersonName = u.getSimpleRealRaceName();
    }
    DefaultMutableTreeNode personNode = createSimpleNode(strPersons, iconPersonName);
    parent.add(personNode);
    expandableNodes.add(new NodeWrapper(personNode, "EMapDetailsPanel.PersonsExpanded"));

    for (PersonTransferRelation itr : u.getPersonTransferRelations()) {
      String prefix = String.valueOf(itr.amount) + " ";
      String addIcon = null;
      Unit u2 = null;

      if (itr.source == u) {
        addIcon = "get";
        u2 = itr.target;
      } else if (itr.target == u) {
        addIcon = "give";
        u2 = itr.source;
      }

      if (u2 != null) {
        UnitNodeWrapper unw =
            nodeWrapperFactory.createUnitNodeWrapper(u2, prefix, u2.getPersons(), u2
                .getModifiedPersons());
        unw.setAdditionalIcon(addIcon);
        unw.setReverseOrder(true);
        personNode.add(new DefaultMutableTreeNode(unw));
      }
    }

    for (UnitTransferRelation relation : u.getRelations(UnitTransferRelation.class)) {
      String addIcon = null;
      Unit u2 = null;

      if (relation.source == u) {
        addIcon = "get";
        u2 = relation.target;
      } else if (relation.target == u) {
        addIcon = "give";
        u2 = relation.source;
      }

      UnitNodeWrapper unw = nodeWrapperFactory.createUnitNodeWrapper(u2);
      unw.setAdditionalIcon(addIcon);
      unw.setReverseOrder(true);
      personNode.add(new DefaultMutableTreeNode(unw));
    }
  }

  /**
   * Appends information about this unit's faction.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitFactionInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    appendFactionInfo(u.getFaction(), u.getGroup() == null ? u.getFaction().getAllies() : u
        .getGroup().allies(), u.getFaction().getAlliance(), parent, expandableNodes);
  }

  /**
   * Appends information about this faction.
   * 
   * @param alliance
   * @param allies
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendFactionInfo(Faction f, Map<EntityID, Alliance> allies, AllianceGroup alliance,
      DefaultMutableTreeNode parent, Collection<NodeWrapper> expandableNodes) {
    DefaultMutableTreeNode fNode;
    if (f == null) {
      fNode =
          new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
              .get("emapdetailspanel.node.faction")
              + ": " + Resources.get("emapdetailspanel.node.unknownfaction"), "faction"));
    } else {
      // custom faction icon ?
      String customFactionIconFileName = "custom/factions/" + f.getID();
      if (isAllowingCustomIcons()
          && getMagellanContext().getImageFactory().existImageIcon(customFactionIconFileName)) {
        if (getMagellanContext().getImageFactory().imageIconSizeCheck(customFactionIconFileName,
            40, 40)) {
          fNode = createSimpleNode(f.toString(), customFactionIconFileName);
        } else {
          fNode = createSimpleNode(f.toString(), "toobig");
        }

      } else {
        fNode =
            new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
                .get("emapdetailspanel.node.faction")
                + ": " + f.toString(), "faction"));
      }

      parent.add(fNode);

      // ALLIES
      if (allies != null) {
        DefaultMutableTreeNode n =
            new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.alliances"));
        fNode.add(n);
        expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.AlliancesExpanded"));

        FactionStatsPanel.showAlliances(data, allies, alliance, n);
      }

      if (f.getTreasury() > 0) {
        String text = Resources.get("emapdetailspanel.node.treasury", f.getTreasury());
        parent.add(createSimpleNode(text, "reichsschatz"));
      }
    }
  }

  /**
   * Appends information on the unit's race.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitRaceInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if (u.getRace() != null) {
      StringBuffer nodeText =
          new StringBuffer(Resources.get("emapdetailspanel.node.race")).append(": ");

      if (u.getDisguiseRace() != null) {
        nodeText.append(u.getRaceName(data));
        nodeText.append(" (").append(u.getRace().getName()).append(")");
      } else {
        nodeText.append(u.getRaceName(data));
      }

      parent.add(createSimpleNode(nodeText.toString(), "rasse"));
    }
  }

  /**
   * Appends information on the unit's group.
   * 
   * @param u
   */
  private void appendUnitGroupInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if (u.getGroup() != null) {
      Group group = u.getGroup();
      DefaultMutableTreeNode groupNode =
          new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
              .get("emapdetailspanel.node.group")
              + ": \"" + group.getName() + "\"", "groups"));

      parent.add(groupNode);
      // expandableNodes.add(new NodeWrapper(groupNode, "EMapDetailsPanel.UnitGroupExpanded"));
      appendAlliances(group.allies(), groupNode);
    }

  }

  /**
   * Append Information on the stealth level etc. for this unit.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitStealthInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // faction hidden, stealth level
    Skill stealth = u.getSkill(data.rules.getSkillType(EresseaConstants.S_TARNUNG, true));
    int stealthLevel = 0;

    if (stealth != null) {
      stealthLevel = stealth.getLevel();
    }

    if (u.isHideFaction() || stealthLevel > 0) {
      String strHide = Resources.get("emapdetailspanel.node.stealth") + ":";

      if (stealthLevel > 0) {
        if (u.getStealth() == -1) {
          strHide += (" " + stealthLevel);
        } else {
          strHide += (" " + u.getStealth() + " / " + stealthLevel);
        }
      }

      if (u.isHideFaction()) {
        if (stealthLevel > 0) {
          strHide += ",";
        }

        strHide += (" " + Resources.get("emapdetailspanel.node.disguised"));
      }

      String icon = null;

      if (stealth != null && stealth.getSkillType() != null
          && stealth.getSkillType().getID() != null) {
        icon = stealth.getSkillType().getID().toString();
      } else {
        icon = "tarnung";
      }

      SimpleNodeWrapper wrapper = nodeWrapperFactory.createSimpleNodeWrapper(strHide, icon);

      if ((stealthLevel > 0) && magellan.library.utils.Units.isPrivilegedAndNoSpy(u)) {
        wrapper.setContextFactory(stealthContext);
        wrapper.setArgument(u);
      }

      parent.add(new DefaultMutableTreeNode(wrapper));
    }
    // disguise
    if (u.getGuiseFaction() != null) {
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.disguisedas") + " "
          + u.getGuiseFaction(), ((stealth != null) ? stealth.getSkillType().getID().toString()
          : "tarnung")));
    }

    /*
     * DefaultMutableTreeNode n = null; if(!u.getRegion().resources().isEmpty()) { // resources of
     * region for(Iterator iter = u.getRegion().resources().iterator(); iter.hasNext();) {
     * RegionResource res = (RegionResource) iter.next(); ItemType resItemType = res.getType(); if
     * (resItemType!=null && resItemType.getMakeSkill()!=null) { Skill resMakeSkill =
     * resItemType.getMakeSkill(); SkillType resMakeSkillType = resMakeSkill.getSkillType(); if
     * (resMakeSkillType!=null && u.getModifiedSkill(resMakeSkillType)!=null){ Skill unitSkill =
     * u.getModifiedSkill(resMakeSkillType); if (unitSkill.getLevel()>0){ if (n==null){ n=new
     * DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.resources_region"));
     * expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.UnitRegionResourceExpanded")); } int
     * oldValue = findOldValueByResourceType(u.getRegion(), res); appendResource(res, n, oldValue);
     * } } } } } if (n!=null){ parent.add(n); }
     */
  }

  private void appendFamiliarInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // familiar mages (Fiete)
    // we use auraMax to determine a mage...should be OK
    if (u.getAuraMax() != -1) {
      if (u.getFamiliarmageID() != null) {
        // ok..this unit is a familiar (Vertrauter)
        // get the parent unit

        Unit parentUnit = data.getUnit(u.getFamiliarmageID());
        if (parentUnit != null) {
          // DefaultMutableTreeNode parentsNode = new DefaultMutableTreeNode("Parents");
          DefaultMutableTreeNode parentsNode =
              new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
                  .get("emapdetailspanel.node.FamiliarParents"), "aura"));
          parent.add(parentsNode);
          expandableNodes.add(new NodeWrapper(parentsNode,
              "EMapDetailsPanel.FamiliarParentsExpanded"));
          UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(parentUnit);
          DefaultMutableTreeNode parentUnitNode = new DefaultMutableTreeNode(w);
          parentsNode.add(parentUnitNode);
        }
      } else {
        // ok..we have a real mage..may be he has an familiar...
        // for further purpose lets look for all familiars...
        Collection<Unit> familiars = new LinkedList<Unit>();
        for (UnitID uID : data.units().keySet()) {
          Unit uTest = data.getUnit(uID);
          if (uTest.getFamiliarmageID() == u.getID()) {
            familiars.add(uTest);
          }
        }
        if (familiars.size() > 0) {
          DefaultMutableTreeNode childsNode =
              new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
                  .get("emapdetailspanel.node.FamiliarChilds"), "aura"));
          parent.add(childsNode);
          expandableNodes
              .add(new NodeWrapper(childsNode, "EMapDetailsPanel.FamiliarChildsExpanded"));
          for (Unit uT : familiars) {
            UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(uT);
            DefaultMutableTreeNode parentUnitNode = new DefaultMutableTreeNode(w);
            childsNode.add(parentUnitNode);
          }
        }
      }
    }
  }

  /**
   * Append information on the unit's weight.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitWeight(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    float uWeight = getGameSpecificStuff().getMovementEvaluator().getWeight(u) / 100.0F;
    float modUWeight = getGameSpecificStuff().getMovementEvaluator().getModifiedWeight(u) / 100.0F;
    String text =
        Resources.get("emapdetailspanel.node.totalweight") + ": "
            + EMapDetailsPanel.weightNumberFormat.format(uWeight);

    if (uWeight != modUWeight) {
      text += (" (" + EMapDetailsPanel.weightNumberFormat.format(modUWeight) + ")");
    }

    text += (" " + Resources.get("emapdetailspanel.node.weightunits"));
    parent.add(createSimpleNode(text, "gewicht"));
  }

  /**
   * Append information on the possible horses.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitHorses(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    int maxHorsesWalking = getRules().getMaxHorsesWalking(u);
    int maxHorsesRiding = getRules().getMaxHorsesRiding(u);

    String text = "Max: " + maxHorsesWalking + " / " + maxHorsesRiding;
    parent.add(createSimpleNode(text, "pferd"));
  }

  /**
   * Append information on this unit's payload and capacity.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitLoadInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // load
    int load = getGameSpecificStuff().getMovementEvaluator().getLoad(u);
    int modLoad = getGameSpecificStuff().getMovementEvaluator().getModifiedLoad(u);

    if ((load != 0) || (modLoad != 0)) {
      String text =
          Resources.get("emapdetailspanel.node.load") + ": "
              + EMapDetailsPanel.weightNumberFormat.format(new Float(load / 100.0F));

      if (load != modLoad) {
        text +=
            (" (" + EMapDetailsPanel.weightNumberFormat.format(new Float(modLoad / 100.0F)) + ")");
      }

      text += (" " + Resources.get("emapdetailspanel.node.weightunits"));
      parent.add(createSimpleNode(text, "beladung"));
    }

    // payload
    int maxOnFoot = getGameSpecificStuff().getMovementEvaluator().getPayloadOnFoot(u);

    if (maxOnFoot == Unit.CAP_UNSKILLED) {
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.capacityonfoot") + ": "
          + Resources.get("emapdetailspanel.node.toomanyhorses"), "warnung"));
    } else {
      float max = maxOnFoot / 100.0F;
      float free = Math.abs(maxOnFoot - modLoad) / 100.0F;
      DefaultMutableTreeNode capacityNode;

      if ((maxOnFoot - modLoad) < 0) {
        capacityNode =
            new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
                .get("emapdetailspanel.node.capacityonfoot")
                + ": "
                + Resources.get("emapdetailspanel.node.overloadedby")
                + " "
                + EMapDetailsPanel.weightNumberFormat.format(free)
                + " "
                + Resources.get("emapdetailspanel.node.weightunits"), "warnung"));
      } else {
        capacityNode =
            new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
                .get("emapdetailspanel.node.capacityonfoot")
                + ": "
                + EMapDetailsPanel.weightNumberFormat.format(free)
                + " / "
                + EMapDetailsPanel.weightNumberFormat.format(max)
                + " "
                + Resources.get("emapdetailspanel.node.weightunits"), "ladfuss"));
        appendUnitCapacityByItems(capacityNode, u, maxOnFoot - modLoad);

        if (capacityNode.getChildCount() > 0) {
          expandableNodes.add(new NodeWrapper(capacityNode,
              "EMapDetailsPanel.UnitCapacityOnFootExpanded"));
        }
      }

      parent.add(capacityNode);
    }

    int maxOnHorse = getGameSpecificStuff().getMovementEvaluator().getPayloadOnHorse(u);

    if (maxOnHorse == Unit.CAP_UNSKILLED) {
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.capacityonhorse") + ": "
          + Resources.get("emapdetailspanel.node.toomanyhorses"), "warnung"));
    } else if (maxOnHorse == Unit.CAP_NO_HORSES) {
    } else {
      float max = maxOnHorse / 100.0F;
      float free = Math.abs(maxOnHorse - modLoad) / 100.0F;
      DefaultMutableTreeNode capacityNode;

      if ((maxOnHorse - modLoad) < 0) {
        capacityNode =
            new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
                .get("emapdetailspanel.node.capacityonhorse")
                + ": "
                + Resources.get("emapdetailspanel.node.overloadedby")
                + " "
                + EMapDetailsPanel.weightNumberFormat.format(free)
                + " "
                + Resources.get("emapdetailspanel.node.weightunits"), "warnung"));
      } else {
        capacityNode =
            new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
                .get("emapdetailspanel.node.capacityonhorse")
                + ": "
                + EMapDetailsPanel.weightNumberFormat.format(free)
                + " / "
                + EMapDetailsPanel.weightNumberFormat.format(max)
                + " "
                + Resources.get("emapdetailspanel.node.weightunits"), "ladpferd"));
        appendUnitCapacityByItems(capacityNode, u, maxOnHorse - modLoad);

        if (capacityNode.getChildCount() > 0) {
          expandableNodes.add(new NodeWrapper(capacityNode,
              "EMapDetailsPanel.UnitCapacityOnHorseExpanded"));
        }
      }

      parent.add(capacityNode);
    }
  }

  private void appendUnitItemInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // items
    if (u.getModifiedItems().size() > 0) {
      // DefaultMutableTreeNode itemsNode = new
      // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.items"));
      DefaultMutableTreeNode itemsNode =
          createSimpleNode(Resources.get("emapdetailspanel.node.items"), "things");
      parent.add(itemsNode);
      expandableNodes.add(new NodeWrapper(itemsNode, "EMapDetailsPanel.UnitItemsExpanded"));

      Collection<TreeNode> catNodes =
          unitsTools.addCategorizedUnitItems(Collections.singleton(u), itemsNode, null, null,
              false, nodeWrapperFactory, relationContext);
      if (catNodes != null) {
        for (TreeNode treeNode : catNodes) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode;
          Object o = node.getUserObject();
          ID id = null;
          if (o instanceof ItemCategoryNodeWrapper) {
            id = (((ItemCategoryNodeWrapper) o).getItemCategory()).getID();
          } else {
            if (o instanceof ItemCategory) {
              id = ((ItemCategory) o).getID();
            } else {
              if (o instanceof ItemNodeWrapper) {
                id = ((ItemNodeWrapper) o).getItem().getItemType().getID();
              }
            }
          }

          if (id != null) {
            expandableNodes.add(new NodeWrapper(node, "EMapDetailsPanel.UnitItems" + id
                + "Expanded"));
          }
        }
      }
    }
  }

  /**
   * Append information about all things on the ship
   * 
   * @param s The ship
   * @param parent Mother-Tree-Node
   * @param expandableNodes
   */
  private void appendShipItemInfo(Ship s, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // items
    List<Unit> units = new LinkedList<Unit>(s.units()); // the collection of units currently on the
    // ship
    Collections.sort(units, sortIndexComparator);

    Collection<Unit> modUnits = s.modifiedUnits(); // the collection of units on the ship in the
    // next turn
    Collection<Unit> allInmates = new LinkedList<Unit>();
    allInmates.addAll(units);
    for (Unit aU : modUnits) {
      if (!allInmates.contains(aU)) {
        allInmates.add(aU);
      }
    }

    // DefaultMutableTreeNode itemsNode = new
    // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.items"));
    DefaultMutableTreeNode itemsNode =
        createSimpleNode(Resources.get("emapdetailspanel.node.items"), "things");
    parent.add(itemsNode);
    expandableNodes.add(new NodeWrapper(itemsNode, "EMapDetailsPanel.ShipItemsExpanded"));

    Collection<TreeNode> catNodes =
        unitsTools.addCategorizedUnitItems(allInmates, itemsNode, null, null, true,
            nodeWrapperFactory, null);
    if (catNodes != null) {
      for (TreeNode treeNode : catNodes) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode;
        Object o = node.getUserObject();
        ID id = null;
        if (o instanceof ItemCategoryNodeWrapper) {
          id = (((ItemCategoryNodeWrapper) o).getItemCategory()).getID();
        } else {
          if (o instanceof ItemCategory) {
            id = ((ItemCategory) o).getID();
          } else {
            if (o instanceof ItemNodeWrapper) {
              id = ((ItemNodeWrapper) o).getItem().getItemType().getID();
            }
          }
        }

        if (id != null) {
          expandableNodes
              .add(new NodeWrapper(node, "EMapDetailsPanel.ShipItems" + id + "Expanded"));
        }
      }
    }
  }

  /**
   * Append information on the skills of this unit.
   * 
   * @return <code>true</code> iff <code>u</code> is a trader
   */
  private boolean appendUnitSkillInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // skills
    boolean isTrader = false;
    SkillCategory tradeCat = data.rules.getSkillCategory(StringID.create("trade"));
    SkillType tradeSkill = null;

    if (tradeCat == null) {
      tradeSkill = data.rules.getSkillType(EresseaConstants.S_HANDELN);
    }

    Collection<Skill> modSkills = u.getModifiedSkills();
    List<Skill> sortedSkills = null;

    if ((modSkills != null) && (modSkills.size() > 0)) {
      sortedSkills = new LinkedList<Skill>(modSkills);
    } else {
      sortedSkills = new LinkedList<Skill>(u.getSkills());
    }

    Collections.sort(sortedSkills, new SkillComparator());

    if (!sortedSkills.isEmpty()) {
      // DefaultMutableTreeNode skillsNode = new
      // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.skills"));
      DefaultMutableTreeNode skillsNode =
          new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
              .get("emapdetailspanel.node.skills"), "skills"));
      parent.add(skillsNode);
      expandableNodes.add(new NodeWrapper(skillsNode, "EMapDetailsPanel.UnitSkillsExpanded"));

      for (Iterator<Skill> iter = sortedSkills.iterator(); iter.hasNext();) {
        Skill os = null;
        Skill ms = null;

        if (modSkills != null) {
          // assume that we are iterating over the mod skills
          ms = iter.next();
          os = u.getSkill(ms.getSkillType());
        } else {
          // assume that we are iterating over the original skills
          os = iter.next();
        }

        if (!isTrader && (os != null)) { // check for trader

          if ((tradeCat != null) && tradeCat.isInstance(os.getSkillType())) {
            isTrader = true;
          } else if ((tradeSkill != null) && tradeSkill.equals(os.getSkillType())) {
            isTrader = true;
          }
        }

        skillsNode.add(new DefaultMutableTreeNode(nodeWrapperFactory.createSkillNodeWrapper(u, os,
            ms)));
      }
    }
    return isTrader;
  }

  /**
   * Append information on this unit's teachers and pupils.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitTeachInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // teacher pupil relations
    Collection<Unit> pupils = new LinkedList<Unit>();
    Collection<Unit> teachers = new LinkedList<Unit>();

    pupils = u.getPupils();
    teachers = u.getTeachers();
    // for(Iterator iter = u.getRelations(TeachRelation.class).iterator(); iter.hasNext();) {
    // TeachRelation tr = (TeachRelation) iter.next();
    //
    // if(u.equals(tr.source)) {
    // if(tr.target != null) {
    // pupils.add(tr.target);
    // }
    // } else {
    // teachers.add(tr.source);
    // }
    // }

    if (teachers.size() > 0) {
      // DefaultMutableTreeNode teachersNode = new DefaultMutableTreeNode("");
      DefaultMutableTreeNode teachersNode = createSimpleNode("", "teacher");
      parent.add(teachersNode);
      expandableNodes.add(new NodeWrapper(teachersNode, "EMapDetailsPanel.UnitTeachersExpanded"));

      int teacherCounter = 0;

      for (Unit teacher : teachers) {
        teacherCounter += teacher.getModifiedPersons();

        UnitNodeWrapper w =
            nodeWrapperFactory.createUnitNodeWrapper(teacher, teacher.getPersons(), teacher
                .getModifiedPersons());
        DefaultMutableTreeNode teacherNode = new DefaultMutableTreeNode(w);
        teachersNode.add(teacherNode);
      }

      int teachFactor = data.getGameSpecificStuff().getGameSpecificRules().getTeachFactor();
      teachersNode.setUserObject(new UnitListNodeWrapper(Resources
          .get("emapdetailspanel.node.teacher")
          + ": "
          + teacherCounter
          + " / "
          + (((u.getModifiedPersons() % teachFactor) == 0) ? (u.getModifiedPersons() / teachFactor)
              : ((u.getModifiedPersons() / teachFactor) + 1)), null, teachers, "teacher"));
    }

    if (pupils.size() > 0) {
      boolean duplicatePupil = false;
      Collection<Unit> checkPupils = new LinkedList<Unit>();
      // DefaultMutableTreeNode pupilsNode = new DefaultMutableTreeNode("");
      DefaultMutableTreeNode pupilsNode = createSimpleNode("", "pupils");
      parent.add(pupilsNode);
      expandableNodes.add(new NodeWrapper(pupilsNode, "EMapDetailsPanel.UnitPupilsExpanded"));

      int pupilCounter = 0;

      for (Unit pupil : pupils) {
        pupilCounter += pupil.getModifiedPersons();

        UnitNodeWrapper w =
            nodeWrapperFactory.createUnitNodeWrapper(pupil, pupil.getPersons(), pupil
                .getModifiedPersons());
        DefaultMutableTreeNode pupilNode = new DefaultMutableTreeNode(w);
        pupilsNode.add(pupilNode);

        // duplicate check
        if (checkPupils.contains(pupil)) {
          duplicatePupil = true;
        } else {
          checkPupils.add(pupil);
        }

      }

      String duplicatePupilWarning = "";
      if (duplicatePupil) {
        duplicatePupilWarning = " (!!!)";
      }

      pupilsNode.setUserObject(new UnitListNodeWrapper(Resources
          .get("emapdetailspanel.node.pupils")
          + ": "
          + pupilCounter
          + " / "
          + (u.getModifiedPersons() * data.getGameSpecificStuff().getGameSpecificRules()
              .getTeachFactor()) + duplicatePupilWarning, null, pupils, "pupils"));
    }
  }

  private void appendUnitTransportationInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // transportation relations
    Collection<Unit> carriers = u.getCarriers();

    if (carriers.size() > 0) {
      DefaultMutableTreeNode carriersNode =
          new DefaultMutableTreeNode(new UnitListNodeWrapper(Resources
              .get("emapdetailspanel.node.carriers"), null, carriers, "carriers"));
      parent.add(carriersNode);
      expandableNodes.add(new NodeWrapper(carriersNode, "EMapDetailsPanel.UnitCarriersExpanded"));

      for (Unit carrier : carriers) {
        UnitNodeWrapper w =
            nodeWrapperFactory.createUnitNodeWrapper(carrier, carrier.getPersons(), carrier
                .getModifiedPersons());
        carriersNode.add(new DefaultMutableTreeNode(w));
      }
    }
    Collection<Unit> passengers = u.getPassengers();

    if (passengers.size() > 0) {
      DefaultMutableTreeNode passengersNode =
          new DefaultMutableTreeNode(new UnitListNodeWrapper(Resources
              .get("emapdetailspanel.node.passengers"), null, passengers, "passengers"));
      parent.add(passengersNode);
      expandableNodes
          .add(new NodeWrapper(passengersNode, "EMapDetailsPanel.UnitPassengersExpanded"));

      for (Unit passenger : passengers) {
        int pweight = getGameSpecificStuff().getMovementEvaluator().getWeight(passenger);
        int pmodweight = getGameSpecificStuff().getMovementEvaluator().getModifiedWeight(passenger);
        String str =
            passenger.toString() + ": "
                + EMapDetailsPanel.weightNumberFormat.format(new Float(pweight / 100.0f)) + " "
                + Resources.get("emapdetailspanel.node.weightunits");

        if (pweight != pmodweight) {
          str +=
              (" (" + EMapDetailsPanel.weightNumberFormat.format(new Float(pmodweight / 100.0f))
                  + " " + Resources.get("emapdetailspanel.node.weightunits") + ")");
        }

        UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(passenger, str);
        passengersNode.add(new DefaultMutableTreeNode(w));
      }
    }
  }

  private void appendUnitAttackInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // attack relations

    Collection<Unit> attacks = u.getAttackVictims();

    if (attacks.size() > 0) {
      DefaultMutableTreeNode attacksNode =
          new DefaultMutableTreeNode(new UnitListNodeWrapper(Resources
              .get("emapdetailspanel.node.attacks"), null, attacks, "victim"));
      parent.add(attacksNode);
      expandableNodes.add(new NodeWrapper(attacksNode, "EMapDetailsPanel.UnitAttacksExpanded"));

      for (Unit victim : attacks) {
        UnitNodeWrapper w =
            nodeWrapperFactory.createUnitNodeWrapper(victim, victim.getPersons(), victim
                .getModifiedPersons());
        attacksNode.add(new DefaultMutableTreeNode(w));
      }
    }

    Collection<Unit> attackedBy = u.getAttackAggressors();

    if (attackedBy.size() > 0) {
      DefaultMutableTreeNode attackedByNode =
          new DefaultMutableTreeNode(new UnitListNodeWrapper(Resources
              .get("emapdetailspanel.node.attackedBy"), null, attackedBy, "attacker"));
      parent.add(attackedByNode);
      expandableNodes
          .add(new NodeWrapper(attackedByNode, "EMapDetailsPanel.UnitAttackedByExpanded"));

      for (Unit victim : attackedBy) {
        UnitNodeWrapper w =
            nodeWrapperFactory.createUnitNodeWrapper(victim, victim.getPersons(), victim
                .getModifiedPersons());
        attackedByNode.add(new DefaultMutableTreeNode(w));
      }
    }
  }

  /**
   * Append information on the unit's spells
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitSpellsInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if ((u.getSpells() != null) && (u.getSpells().size() > 0)) {
      // DefaultMutableTreeNode spellsNode = new
      // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.spells"));
      DefaultMutableTreeNode spellsNode =
          createSimpleNode(Resources.get("emapdetailspanel.node.spells"), "magicschool");
      parent.add(spellsNode);
      expandableNodes.add(new NodeWrapper(spellsNode, "EMapDetailsPanel.UnitSpellsExpanded"));

      List<Spell> sortedSpells = new LinkedList<Spell>(u.getSpells().values());
      Collections.sort(sortedSpells, new SpellLevelComparator(new NameComparator(null)));

      for (Spell spell : sortedSpells) {
        // do not use Named variant here; we want to use Spell.toString() instead of Spell.getName()
        // in order to display spell level, type, etc.
        spellsNode.add(createSimpleNode((Object) spell, "spell"));
      }
    }
  }

  private void appendUnitCombatSpells(Map<? extends ID, CombatSpell> spells,
      DefaultMutableTreeNode parent, Collection<NodeWrapper> expandableNodes) {
    if ((spells == null) || spells.isEmpty())
      return;

    // DefaultMutableTreeNode combatSpells = new
    // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.combatspells"));
    DefaultMutableTreeNode combatSpells =
        createSimpleNode(Resources.get("emapdetailspanel.node.combatspells"), "combatspell");
    parent.add(combatSpells);
    expandableNodes.add(new NodeWrapper(combatSpells, "EMapDetailsPanel.UnitCombatSpellsExpanded"));

    for (CombatSpell spell : spells.values()) {
      combatSpells.add(createSimpleNode(spell, "spell"));
    }
  }

  /**
   * Appends information on the potions that this unit can make.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitPotionInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if ((data.potions() != null) && (u.getSkillMap() != null)
        && u.getSkillMap().containsKey(EresseaConstants.S_ALCHEMIE)) {
      // DefaultMutableTreeNode potionsNode = new
      // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.potions"));
      DefaultMutableTreeNode potionsNode =
          createSimpleNode(Resources.get("emapdetailspanel.node.potions"), "Alchemie");
      Skill alchSkill = u.getSkillMap().get(EresseaConstants.S_ALCHEMIE);
      List<Potion> potions = new LinkedList<Potion>(data.getPotions());
      Collections.sort(potions, new PotionLevelComparator(new NameComparator(null)));

      // we have after merging multiple potion-definitions within the CR
      // lets build a list of potions to show
      LinkedList<Potion> potionList = null;

      for (Potion p : potions) {
        if ((p.getLevel() * 2) <= alchSkill.getLevel()) {

          // lets add this potion to the list of potions to show
          if (potionList == null) {
            potionList = new LinkedList<Potion>();
          }
          if (!potionList.contains(p)) {
            potionList.add(p);
          }

        }
      }
      // lets see if we have potions to display
      if (potionList != null && potionList.size() > 0) {
        for (Potion p : potionList) {
          int max = getBrewablePotions(data.rules, p, u.getRegion());
          potionsNode.add(new DefaultMutableTreeNode(nodeWrapperFactory.createPotionNodeWrapper(p,
              data.getTranslation(p), ": " + max)));
        }
      }

      if (potionsNode.getChildCount() > 0) {
        parent.add(potionsNode);
        expandableNodes.add(new NodeWrapper(potionsNode, "EMapDetailsPanel.UnitPotionsExpanded"));
      }
    }
  }

  /**
   * Append information on the region resources if this unit is a maker.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendUnitRegionResource(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    DefaultMutableTreeNode resourceNode = null;
    if (u.getRegion() != null && !u.getRegion().resources().isEmpty()) {
      for (RegionResource res : u.getRegion().resources()) {
        ItemType resItemType = res.getType();
        if (resItemType != null && resItemType.getMakeSkill() != null) {
          Skill resMakeSkill = resItemType.getMakeSkill();
          SkillType resMakeSkillType = resMakeSkill.getSkillType();
          if (resMakeSkillType != null && u.getModifiedSkill(resMakeSkillType) != null) {
            Skill unitSkill = u.getModifiedSkill(resMakeSkillType);
            if (unitSkill.getLevel() > 0) {
              if (resourceNode == null) {
                // resourceNode=new
                // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.resources_region"));
                resourceNode =
                    createSimpleNode(Resources.get("emapdetailspanel.node.resources_region"),
                        "ressourcen");
                expandableNodes.add(new NodeWrapper(resourceNode,
                    "EMapDetailsPanel.UnitRegionResourceExpanded"));
              }
              int oldValue = findOldValueByResourceType(u.getRegion(), res);
              appendResource(res, resourceNode, oldValue);
            }
          }
        }
      }
    }

    if (resourceNode != null) {
      parent.add(resourceNode);
    }
  }

  /**
   * Append information on the containers this unit is in, if it is commanding any and who it is
   * passing command.
   * 
   * @param u
   * @param parent
   * @param expandableNodes
   */
  private void appendContainerInfo(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if (u.getUnitContainer() != null) {
      DefaultMutableTreeNode containerNode = null;
      boolean isOwner = u.getUnitContainer().getOwnerUnit() == u;
      UnitContainerNodeWrapper cnw =
          nodeWrapperFactory.createUnitContainerNodeWrapper(u.getUnitContainer(), true, isOwner);
      containerNode = new DefaultMutableTreeNode(cnw);
      parent.add(containerNode);
    }
    if (u.getModifiedUnitContainer() != null
        && !u.getModifiedUnitContainer().equals(u.getUnitContainer())) {
      DefaultMutableTreeNode containerNode = null;
      boolean isOwner = false; // TODO: can we calculate if the unit is going to be the
      // owner of its new container?
      containerNode =
          new DefaultMutableTreeNode(nodeWrapperFactory.createUnitContainerNodeWrapper(u
              .getModifiedUnitContainer(), true, isOwner));
      parent.add(containerNode);
    }

    // command relations
    DefaultMutableTreeNode commandNode = null;
    for (ControlRelation rel : u.getRelations(ControlRelation.class)) {
      if (commandNode == null) {
        commandNode = new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.command"));
      }
      expandableNodes.add(new NodeWrapper(commandNode, "EMapDetailsPanel.PersonsExpanded"));
      if (rel.target == u) {
        Unit u2 = rel.source;
        UnitNodeWrapper unw;
        if (rel.warning) {
          unw = nodeWrapperFactory.createUnitNodeWrapper(u2, "(!!!) " + u2);
        } else {
          unw = nodeWrapperFactory.createUnitNodeWrapper(u2);
        }
        unw.setAdditionalIcon("give");
        unw.setReverseOrder(true);
        commandNode.add(new DefaultMutableTreeNode(unw));
      }
      if (rel.source == u) {
        Unit u2 = rel.target;
        UnitNodeWrapper unw;
        if (rel.warning) {
          unw = nodeWrapperFactory.createUnitNodeWrapper(u2, "(!!!) " + u2);
        } else {
          unw = nodeWrapperFactory.createUnitNodeWrapper(u2);
        }
        unw.setAdditionalIcon("get");
        unw.setReverseOrder(true);
        commandNode.add(new DefaultMutableTreeNode(unw));
      }
      parent.add(commandNode);
    }
  }

  private void appendUnitCapacityByItems(DefaultMutableTreeNode parent, Unit u, int freeCapacity) {
    ItemType carts = data.rules.getItemType(EresseaConstants.I_CART);
    ItemType silver = data.rules.getItemType(EresseaConstants.I_USILVER);
    // Fiete: feature request...showing not only capacity for "good" items in region...
    if (PropertiesHelper.getBoolean(settings, "unitCapacityContextMenuShowFriendly", true)
        && u.getRegion() != null) {
      for (Item item : magellan.library.utils.Units.getContainerPrivilegedUnitItems(u.getRegion())) {
        ItemType type = item.getItemType();

        if ((type.getWeight() > 0.0) && !type.isHorse() && !type.equals(carts)
            && !type.equals(silver)) {
          int weight = (int) (type.getWeight() * 100);
          parent.add(createSimpleNode("Max. " + type.getName() + ": " + (freeCapacity / weight),
              "items/" + type.getIconName()));
        }
      }
    }

    if (PropertiesHelper.getBoolean(settings, "unitCapacityContextMenuShowSome", false)
        && u.getRegion() != null) {
      for (Item item : magellan.library.utils.Units.getContainerAllUnitItems(u.getRegion())) {
        ItemType type = item.getItemType();

        if ((type.getWeight() > 0.0) && !type.isHorse() && !type.equals(carts)
            && !type.equals(silver)) {
          int weight = (int) (type.getWeight() * 100);
          parent.add(createSimpleNode("Max. " + type.getName() + ": " + (freeCapacity / weight),
              "items/" + type.getIconName()));
        }
      }
    }

    if (PropertiesHelper.getBoolean(settings, "unitCapacityContextMenuShowAll", false)) {
      // show all itemtypes...need to built and sort a list
      // we take natural order - it works - added Comparable to ItemType (Fiete)
      TreeSet<ItemType> l = new TreeSet<ItemType>();
      // only use the items found in gamedata
      // without fancy merging, for all items should pe Translation present
      for (Region r : getGameData().getRegions()) {
        for (Item item : magellan.library.utils.Units.getContainerAllUnitItems(r)) {
          ItemType type = item.getItemType();
          l.add(type);
        }
      }
      String actLocale = getGameData().getLocale().toString();
      if (actLocale.equalsIgnoreCase("de")) {
        // ok...a de GameData...here we use all defined ItemTypes from the rules...too
        // need no present Translation for those...in CR all in german...
        for (Iterator<ItemType> iter2 = getGameData().rules.getItemTypeIterator(); iter2.hasNext();) {
          ItemType type = iter2.next();
          l.add(type);
        }
      }

      for (ItemType type : l) {
        if ((type.getWeight() > 0.0) && !type.isHorse() && !type.equals(carts)
            && !type.equals(silver)) {
          int weight = (int) (type.getWeight() * 100);
          parent.add(createSimpleNode("Max. " + type.getName() + ": " + (freeCapacity / weight),
              "items/" + type.getIconName()));
        }
      }
    }

  }

  /**
   * Determines how many potions potion can be brewed from the herbs owned by privileged factions in
   * the specified region.
   */
  private int getBrewablePotions(Rules rules, Potion potion, Region region) {
    int max = Integer.MAX_VALUE;

    for (Item ingredient : potion.ingredients()) {
      if (ingredient.getItemType() != null) {
        // units can not own peasants!
        int amount = 0;

        if (ingredient.getItemType().equals(data.rules.getItemType(StringID.create("Bauer")))) {
          amount = region.getPeasants();
        } else {
          Item item =
              magellan.library.utils.Units.getContainerPrivilegedUnitItem(region, ingredient
                  .getItemType());

          if (item != null) {
            amount = item.getAmount();
          }
        }

        max = Math.min(amount, max);
      }
    }

    return max;
  }

  private void showBuilding(Building b) {
    setNameAndDescription(b, isEditAll()
        || magellan.library.utils.Units.isPrivilegedAndNoSpy(b.getModifiedOwnerUnit()));

    appendBuildingInfo(b, rootNode, myExpandableNodes);
  }

  /**
   * Appends information on a buildig: Type, owner, inmates, maintenance and costs.
   * 
   * @param b
   * @param parent
   * @param expandableNodes
   */
  private void appendBuildingInfo(Building b, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    /*
     * make sure that all unit relations have been established (necessary for enter-/leave
     * relations)
     */
    if (b.getRegion() != null) {
      b.getRegion().refreshUnitRelations();
    }

    // type and true type
    appendBuildingTypeInfo(b, parent, expandableNodes);

    // Besitzer
    appendBuildingOwnerInfo(b, parent, expandableNodes);

    // GIB KOMMANDO?
    appendContainerCommandInfo(b, parent, expandableNodes);

    // Insassen
    appendBuildingInmatesInfo(b, parent, expandableNodes);

    // Gebaeudeunterhalt
    appendBuildingMaintenance(b, parent, expandableNodes);

    // Baukosten
    appendBuildingCosts(b, parent, expandableNodes);

    // Kommentare
    appendComments(b, parent, expandableNodes);
    appendTags(b, parent, expandableNodes);
  }

  /**
   * @param b
   * @param parent
   * @param expandableNodes
   */
  private void appendBuildingCosts(Building b, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    ConstructibleType buildingType = b.getBuildingType();
    if (buildingType == null)
      return;

    int skillLevel = buildingType.getBuildSkillLevel();

    int minSize = -1, maxSize = -1;
    if (buildingType.getMaxSize() > -1) {
      if (buildingType instanceof CastleType) {
        CastleType castleType = (CastleType) buildingType;
        minSize = castleType.getMinSize();
        maxSize = castleType.getMaxSize();
      } else {
        maxSize = buildingType.getMaxSize();
      }
    }
    appendBuildingCosts(b.getBuildingType().getRawMaterials(), skillLevel, minSize, maxSize,
        parent, expandableNodes);
  }

  private void appendBuildingCosts(Collection<Item> rawMaterials, int skillLevel, int minSize,
      int maxSize, DefaultMutableTreeNode parent, Collection<NodeWrapper> expandableNodes) {
    Iterator<Item> iter = rawMaterials.iterator();

    DefaultMutableTreeNode n;
    if (iter.hasNext()) {
      // n = new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.buildingcost"));
      n = createSimpleNode(Resources.get("emapdetailspanel.node.buildingcost"), "buildingcost");
      parent.add(n);
      expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.BuildingCostExpanded"));
    } else
      return;

    DefaultMutableTreeNode m;
    while (iter.hasNext()) {
      Item i = iter.next();
      String text = i.getName();
      m =
          new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(i.getAmount() + " "
              + text, "items/" + i.getItemType().getID().toString()));
      n.add(m);
    }

    if (n != null) {
      // minddestTalent
      if (skillLevel > 0) {
        m =
            new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
                .get("emapdetailspanel.node.buildingminskilllevel")
                + ": " + skillLevel, "skills"));
        n.add(m);
      }

      // maxGr��e (bzw bei Burgen Min-Max)
      // Bei Zitadelle: keine max oder min Gr��enangabe..(kein maxSize verf�gbar)
      if (maxSize >= 0) {
        if (minSize >= 0) {
          m =
              new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources.get(
                  "emapdetailspanel.node.buildingsizelimits", minSize, maxSize), "build_size"));
        } else {
          m =
              new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper("MAX: "
                  + maxSize, "build_size"));
        }
        n.add(m);
      }
    }
  }

  /**
   * @param b
   * @param parent
   * @param expandableNodes
   */
  private void appendBuildingMaintenance(Building b, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    Iterator<Item> iter = b.getBuildingType().getMaintenanceItems().iterator();

    if (iter.hasNext()) {
      // DefaultMutableTreeNode maintNode = new
      // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.upkeep"));
      DefaultMutableTreeNode maintNode =
          createSimpleNode(Resources.get("emapdetailspanel.node.upkeep"), "upkeep");
      parent.add(maintNode);
      expandableNodes
          .add(new NodeWrapper(maintNode, "EMapDetailsPanel.BuildingMaintenanceExpanded"));

      while (iter.hasNext()) {
        Item i = iter.next();
        String text = i.getName();

        DefaultMutableTreeNode m;
        if (text.endsWith(" pro Gr��enpunkt")) {
          int amount = b.getSize() * i.getAmount();
          String newText = text.substring(0, text.indexOf(" pro Gr��enpunkt"));
          m =
              new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(amount + " "
                  + newText, "items/" + i.getItemType().getID().toString()));
        } else {
          m =
              new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(i.getAmount()
                  + " " + text, "items/" + i.getItemType().getID().toString()));
        }

        maintNode.add(m);
      }
    }

  }

  /**
   * Appends information on the inmates of this building.
   * 
   * @param b
   * @param parent
   * @param expandableNodes
   */
  private void appendBuildingInmatesInfo(Building b, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    List<Unit> units = new LinkedList<Unit>(b.units()); // the collection of units currently in the
    // building
    Collections.sort(units, sortIndexComparator);
    Collection<Unit> modUnits = b.modifiedUnits(); // the collection of units in the building in the
    // next turn

    Collection<Unit> allInmates = new LinkedList<Unit>();
    allInmates.addAll(units);
    allInmates.addAll(modUnits);

    int inmates = 0;
    int modInmates = 0;
    if ((units.size() > 0) || (modUnits.size() > 0)) {
      DefaultMutableTreeNode n = new DefaultMutableTreeNode("");
      parent.add(n);
      expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.BuildingInmatesExpanded"));

      DefaultMutableTreeNode m;
      for (Unit u : units) {
        StringBuffer text = new StringBuffer();

        if (b.getModifiedUnit(u.getID()) == null) {
          text.append("<- ");
        }

        text.append(u.toString()).append(": ").append(u.getModifiedPersons());

        UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u, text.toString());
        m = new DefaultMutableTreeNode(w);
        n.add(m);
        inmates += u.getPersons();
      }

      for (Unit u : modUnits) {
        modInmates += u.getModifiedPersons();

        if (b.getUnit(u.getID()) == null) {
          StringBuffer text = new StringBuffer();
          text.append("-> ");
          text.append(u.toString()).append(": ").append(u.getModifiedPersons());

          UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u, text.toString());
          m = new DefaultMutableTreeNode(w);
          n.add(m);
        }
      }

      if (inmates == modInmates) {
        n.setUserObject(new UnitListNodeWrapper(Resources.get("emapdetailspanel.node.inmates")
            + ": " + inmates, null, allInmates, "occupants"));
      } else {
        n.setUserObject(new UnitListNodeWrapper(Resources.get("emapdetailspanel.node.inmates")
            + ": " + inmates + " (" + modInmates + ")", null, allInmates, "occupants"));
      }
    }

    DefaultMutableTreeNode n =
        new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
            ((inmates == modInmates)
                ? (Resources.get("emapdetailspanel.node.size") + ": " + inmates) : (Resources
                    .get("emapdetailspanel.node.size")
                    + ": " + inmates + " (" + modInmates + ")"))
                + " / " + b.getSize(), "build_size"));
    parent.insert(n, 0);

  }

  /**
   * Append information on the owner.
   * 
   * @param b
   * @param parent
   * @param expandableNodes
   */
  private void appendBuildingOwnerInfo(Building b, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if (b.getOwnerUnit() != null) {
      UnitNodeWrapper w =
          nodeWrapperFactory.createUnitNodeWrapper(b.getOwnerUnit(), Resources
              .get("emapdetailspanel.node.owner")
              + ": ", b.getOwnerUnit().getPersons(), b.getOwnerUnit().getModifiedPersons());
      DefaultMutableTreeNode n = new DefaultMutableTreeNode(w);
      parent.add(n);

      if (b.getOwnerUnit().getFaction() == null) {
        n =
            createSimpleNode(Resources.get("emapdetailspanel.node.faction") + ": "
                + Resources.get("emapdetailspanel.node.unknownfaction"), "faction");
      } else {
        n =
            createSimpleNode(Resources.get("emapdetailspanel.node.faction") + ": "
                + b.getOwnerUnit().getFaction().toString(), "faction");
      }
      parent.add(n);
    }

  }

  /**
   * Append information on the buildings type and true type.
   * 
   * @param b
   * @param parent
   * @param expandableNodes
   */
  private void appendBuildingTypeInfo(Building b, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    DefaultMutableTreeNode n;
    // Fiete 20060910
    // added support for wahrerTyp
    if (b.getTrueBuildingType() != null) {
      n = createSimpleNode(Resources.get("emapdetailspanel." + b.getTrueBuildingType()), "warnung");
      parent.add(n);
    }

    // Typ
    n =
        createSimpleNode(
            Resources.get("emapdetailspanel.node.type") + ": " + b.getType().getName(), b.getType()
                .getID().toString());
    parent.add(n);
  }

  /**
   * sets the ship name editable sets the ship description editable shows a tree: (Type : ship.type)
   * (Completion : x % (y/z)) (Coast : ship.direction) (if completed) (Range : node.range (-
   * components) Back - ingredients: 1.name ... n.name
   */
  private void showShip(Ship s) {
    setNameAndDescription(s, isEditAll()
        || magellan.library.utils.Units.isPrivilegedAndNoSpy(s.getModifiedOwnerUnit()));

    appendShipInfo(s, rootNode, myExpandableNodes);
  }

  private void appendShipInfo(Ship s, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    /*
     * make sure that all unit relations have been established (necessary for enter-/leave
     * relations)
     */
    if (s.getRegion() != null) {
      s.getRegion().refreshUnitRelations();
    }

    // Schiffstyp
    if ((s.getName() != null) && (s.getType().getName() != null)) {
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.type") + ": "
          + s.getType().getName(), s.getType().getID().toString()));
    }

    int nominalShipSize = s.getShipType().getMaxSize();

    if (s.getSize() != nominalShipSize) {
      // nominal size and damage
      int ratio = nominalShipSize != 0 ? ratio = (s.getSize() * 100) / nominalShipSize : 0;

      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.completion") + ": " + ratio
          + "% (" + s.getSize() + "/" + nominalShipSize + ")", "sonstiges"));

      appendShipDamageInfo(s, parent, expandableNodes);
    } else {
      // Kueste
      if (s.getShoreId() > -1) {
        parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.shore") + ": "
            + Direction.toString(s.getShoreId()), "shore_" + String.valueOf(s.getShoreId())));
      }

      // Reichweite
      appendShipRangeInfo(s, parent, expandableNodes);

      // damage
      appendShipDamageInfo(s, parent, expandableNodes);

      // Fiete rework: Load and Overload
      appendShipLoadInfo(s, parent, expandableNodes);
    }

    // Besitzer
    appendShipOwnerInfo(s, parent, expandableNodes);

    // Insassen
    appendShipInmateInfo(s, parent, expandableNodes);

    // Things on the ship
    appendShipItemInfo(s, parent, expandableNodes);

    // Baukosten info
    appendShipCosts(s, parent, expandableNodes);

    // Kommentare
    appendComments(s, parent, expandableNodes);
  }

  /**
   * @param s
   * @param parent
   * @param expandableNodes
   */
  private void appendShipCosts(Ship s, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    ShipType shipType = s.getShipType();
    if (shipType == null)
      return;

    int skillLevel = -1;
    if (shipType != null) {
      skillLevel = shipType.getBuildSkillLevel();
    }

    int minSize = -1;
    int maxSize = shipType.getMaxSize();

    appendBuildingCosts(s.getShipType().getRawMaterials(), skillLevel, minSize, maxSize, parent,
        expandableNodes);
  }

  /**
   * Appends information about inmates, modified inmates and their respective weights.
   * 
   * @param s
   * @param parent
   * @param expandableNodes
   */
  private void appendShipInmateInfo(Ship s, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    List<Unit> units = new LinkedList<Unit>(s.units()); // the collection of units currently on the
    // ship
    Collections.sort(units, sortIndexComparator);

    Collection<Unit> modUnits = s.modifiedUnits(); // the collection of units on the ship in the
    // next turn
    Collection<Unit> allInmates = new LinkedList<Unit>();
    allInmates.addAll(units);
    allInmates.addAll(modUnits);

    // sum up load, inmates, modified inmates over all units;
    int inmates = 0;
    int modInmates = 0;
    if ((units.size() > 0) || (modUnits.size() > 0)) {
      DefaultMutableTreeNode n = new DefaultMutableTreeNode("");
      parent.add(n);
      expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.ShipInmatesExpanded"));

      DefaultMutableTreeNode m;
      for (Unit u : units) {
        StringBuffer text = new StringBuffer();

        if (s.getModifiedUnit(u.getID()) == null) {
          text.append("<- ");
        } else {
          // modLoad += u.getModifiedWeight();
        }

        float weight = getGameSpecificStuff().getMovementEvaluator().getWeight(u) / 100.0F;
        float modWeight =
            getGameSpecificStuff().getMovementEvaluator().getModifiedWeight(u) / 100.0f;

        // if (s.getShipType().getMaxPersons() > 0) {
        // weight -= u.getPersons()*u.getRace().getWeight();
        // modWeight -= u.getModifiedPersons()*u.getRace().getWeight();
        // }
        text.append(u.toString()).append(": ").append(
            EMapDetailsPanel.weightNumberFormat.format(weight));

        if (weight != modWeight) {
          text.append(" (").append(EMapDetailsPanel.weightNumberFormat.format(modWeight)).append(
              ")");
        }

        text.append(" ").append(Resources.get("emapdetailspanel.node.weightunits"));

        UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u, text.toString());
        m = new DefaultMutableTreeNode(w);
        n.add(m);
        inmates += u.getPersons();
      }

      for (Unit u : modUnits) {
        modInmates += u.getModifiedPersons();

        if (s.getUnit(u.getID()) == null) {
          StringBuffer text = new StringBuffer();
          float weight = getGameSpecificStuff().getMovementEvaluator().getWeight(u) / 100.0F;
          float modWeight =
              getGameSpecificStuff().getMovementEvaluator().getModifiedWeight(u) / 100.0F;

          // modLoad += u.getModifiedWeight();
          text.append("-> ");
          text.append(u.toString()).append(": ").append(
              EMapDetailsPanel.weightNumberFormat.format(weight));

          if (weight != modWeight) {
            text.append(" (").append(EMapDetailsPanel.weightNumberFormat.format(modWeight)).append(
                ")");
          }

          text.append(" " + Resources.get("emapdetailspanel.node.weightunits"));

          UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u, text.toString());
          m = new DefaultMutableTreeNode(w);
          n.add(m);
        }
      }

      if (inmates == modInmates) {
        n.setUserObject(new UnitListNodeWrapper(Resources.get("emapdetailspanel.node.inmates")
            + ": " + inmates, null, allInmates, "occupants"));
      } else {
        n.setUserObject(new UnitListNodeWrapper(Resources.get("emapdetailspanel.node.inmates")
            + ": " + inmates + " (" + modInmates + ")", null, allInmates, "occupants"));
      }

      if (ShipRoutePlanner.canPlan(s)) { // add a link to the ship route planer
        parent.add(new DefaultMutableTreeNode(new ShipRoutingPlannerButton(s)));
      }
    }
  }

  /**
   * Append nodes for faction and owner.
   * 
   * @param s
   * @param parent
   * @param expandableNodes
   */
  private void appendShipOwnerInfo(Ship s, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    Unit owner = s.getOwnerUnit();

    // owner faction
    if (owner != null) {
      Faction fac = owner.getFaction();
      DefaultMutableTreeNode factionNode;
      if (fac == null) {
        factionNode =
            new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
                .get("emapdetailspanel.node.faction")
                + ": " + Resources.get("emapdetailspanel.node.unknownfaction"), "faction"));
      } else {
        factionNode =
            new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(Resources
                .get("emapdetailspanel.node.faction")
                + ": " + fac.toString(), "faction"));
      }
      parent.add(factionNode);
    }

    // captain

    if (owner != null) {
      UnitNodeWrapper w =
          nodeWrapperFactory.createUnitNodeWrapper(owner, Resources
              .get("emapdetailspanel.node.captain")
              + ": ", owner.getPersons(), owner.getModifiedPersons());
      w.setReverseOrder(true);
      w.setAdditionalIcon("captain");
      DefaultMutableTreeNode ownerNode = new DefaultMutableTreeNode(w);
      parent.add(ownerNode);
      appendContainerCommandInfo(s, ownerNode, expandableNodes);
    }

    // skill

    int captainSkillAmount = magellan.library.utils.Units.getCaptainSkillAmount(s);
    String text =
        Resources.get("emapdetailspanel.node.sailingskill") + ": "
            + Resources.get("emapdetailspanel.node.captain") + " " + captainSkillAmount + " / "
            + s.getShipType().getCaptainSkillLevel() + ", ";
    // n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
    // Resources.get("emapdetailspanel.node.captainskill") + ": " + sailingSkillAmount + " / "
    // + s.getShipType().getCaptainSkillLevel(), "captain"));
    // parent.add(n);

    // Matrosen
    int sailingSkillAmount = magellan.library.utils.Units.getSailingSkillAmount(s);

    text +=
        Resources.get("emapdetailspanel.node.crew") + " " + sailingSkillAmount + " / "
            + s.getShipType().getSailorSkillLevel();

    DefaultMutableTreeNode n =
        new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(text, "crew"));
    parent.add(n);

  }

  /**
   * Append nodes for ship load and overload
   * 
   * @param s
   * @param parent
   * @param expandableNodes
   */
  private void appendShipLoadInfo(Ship s, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    String strLoad = EMapDetailsPanel.weightNumberFormat.format(new Float(s.getCargo() / 100.0F));

    String strModLoad =
        EMapDetailsPanel.weightNumberFormat.format(new Float(s.getModifiedLoad() / 100.0F));

    String strCap =
        EMapDetailsPanel.weightNumberFormat.format(new Float(s.getMaxCapacity() / 100.0F));

    StringBuffer loadText = new StringBuffer();
    loadText.append(Resources.get("emapdetailspanel.node.load")).append(": ");
    loadText.append(strLoad);
    if (s.getModifiedLoad() != s.getCargo()) {
      loadText.append(" (").append(strModLoad).append(") / ");
    } else {
      loadText.append(" / ");
    }
    loadText.append(strCap).append(" ");
    loadText.append(Resources.get("emapdetailspanel.node.weightunits"));

    boolean warning = false;
    if (s.getShipType().getMaxPersons() > 0) {
      int silverWeight =
          data.rules.getGameSpecificStuff().getGameSpecificRules().getSilverPerWeightUnit();
      int personWeight = 10;
      int maxInmates = s.getMaxPersons() * silverWeight * personWeight; // 10 GE
      loadText.append(" -- ");
      // personen
      int inmates = s.getPersonLoad(), modInmates = s.getModifiedPersonLoad();

      loadText.append(Resources.get("emapdetailspanel.node.persons")).append(": ");
      loadText.append(EMapDetailsPanel.weightNumberFormat.format(new Float(inmates / 100.0F)));
      if (modInmates != inmates) {
        loadText.append(" (").append(
            EMapDetailsPanel.weightNumberFormat.format(new Float(modInmates / 100.0F))).append(
            ") / ");
      } else {
        loadText.append(" / ");
      }
      loadText.append(EMapDetailsPanel.weightNumberFormat.format(new Float(maxInmates / 100.0F)));
      loadText.append(" ").append(Resources.get("emapdetailspanel.node.weightunits"));

      if (modInmates > maxInmates) {
        warning = true;
      }
    }

    if (warning || s.getModifiedLoad() > s.getMaxCapacity()) {
      loadText.append(" (!!!)");
    }

    DefaultMutableTreeNode n =
        new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(loadText.toString(),
            "beladung"));
    parent.add(n);

    // explizit node for overloading a ship
    if (s.getModifiedLoad() > s.getMaxCapacity()) {
      loadText.delete(0, loadText.length());
      loadText.append(Resources.get("emapdetailspanel.node.load")).append(": ");
      loadText.append(Resources.get("emapdetailspanel.node.overloadedby")).append(" ");
      loadText.append(
          EMapDetailsPanel.weightNumberFormat.format(new Float((s.getModifiedLoad() - s
              .getMaxCapacity()) / 100.0F))).append(" ");
      loadText.append(Resources.get("emapdetailspanel.node.weightunits"));

      n =
          new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
              loadText.toString(), "warnung"));
      parent.add(n);
    }

  }

  /**
   * @param s
   * @param parent
   * @param expandableNodes
   */
  private void appendShipRangeInfo(Ship s, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {

    int rad = data.getGameSpecificStuff().getGameSpecificRules().getShipRange(s);

    String rangeString = Resources.get("emapdetailspanel.node.range") + ": " + rad;
    if ((s.getModifiedOwnerUnit() != null) && (s.getModifiedOwnerUnit().getRace() != null)
        && s.getModifiedOwnerUnit().getRace().getAdditiveShipBonus() != 0) {
      rangeString += (" (" + s.getModifiedOwnerUnit().getRace().getName() + ")");
    }

    parent.add(createSimpleNode(rangeString, "radius"));

  }

  /**
   * @param s
   * @param parent
   * @param expandableNodes
   */
  private void appendShipDamageInfo(Ship s, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if (s.getDamageRatio() > 0) {
      int absolute =
          new BigDecimal(s.getDamageRatio() * s.getSize()).divide(new BigDecimal(100),
              BigDecimal.ROUND_UP).intValue();
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.damage") + ": "
          + s.getDamageRatio() + "% / " + absolute, "damage"));
    }
  }

  /**
   * Creates node for GIB KOMMANDO orders for ships and buildings.
   * 
   * @param s
   * @param parent
   * @param expandableNodes
   */
  private void appendContainerCommandInfo(UnitContainer s, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    // command relations
    Set<Unit> givers = null;
    Set<Unit> getters = null;
    for (Unit inmate : s.modifiedUnits()) {
      for (ControlRelation rel : inmate.getRelations(ControlRelation.class)) {
        if (givers == null) {
          givers = new HashSet<Unit>();
        }
        if (getters == null) {
          getters = new HashSet<Unit>();
        }
        if (rel.source == inmate) {
          givers.add(rel.source);
        }
        if (rel.target == inmate) {
          getters.add(rel.target);
        }
      }
    }
    if (givers != null || getters != null) {
      DefaultMutableTreeNode commandNode =
          new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.command"));
      expandableNodes.add(new NodeWrapper(commandNode, "EMapDetailsPanel.PersonsExpanded"));
      if (givers != null) {
        for (Unit u2 : givers) {
          UnitNodeWrapper unw = nodeWrapperFactory.createUnitNodeWrapper(u2);
          unw.setAdditionalIcon("give");
          unw.setReverseOrder(true);
          commandNode.add(new DefaultMutableTreeNode(unw));
        }
      }
      if (getters != null) {
        for (Unit u2 : getters) {
          UnitNodeWrapper unw = nodeWrapperFactory.createUnitNodeWrapper(u2);
          unw.setAdditionalIcon("get");
          unw.setReverseOrder(true);
          commandNode.add(new DefaultMutableTreeNode(unw));
        }
      }
      parent.add(commandNode);
    }
  }

  /**
   * sets the border name not editable sets the border description not editable shows a tree: Type :
   * border.type Direction : border.direction (Completion: border.buildration % (needed stones of
   * all stones)
   */
  private void showBorder(Border b, Region r) {
    setNameAndDescription("", "", false);

    appendBorderInfo(b, r, rootNode, myExpandableNodes);
  }

  private void appendBorderInfo(Border b, Region r, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.type") + ": " + b.getType(), b
        .getType()));
    parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.direction") + ": "
        + Direction.toString(b.getDirection()), "border_" + String.valueOf(b.getDirection())));

    if ((b.getBuildRatio() > -1) && (b.getBuildRatio() != 100)) {
      String str =
          Resources.get("emapdetailspanel.node.completion") + ": " + b.getBuildRatio() + " %";

      if (r != null && Umlaut.normalize(b.getType()).equals("STRASSE")) {
        int stones = r.getRegionType().getRoadStones();
        str +=
            (" (" + ((b.getBuildRatio() * stones) / 100) + " / " + stones + " "
                + data.rules.getItemType(EresseaConstants.I_USTONE).getName() + ")");
      }

      parent.add(new DefaultMutableTreeNode(str));
    }
  }

  /**
   * sets the spells name not editable sets the spells description not editable shows a tree: Type :
   * spell.type Level : spell.level Rank : spell.rank (Ship spells) (Distance spells) (Distance
   * spells) (- components) Back - ingredients: 1.name ... n.name
   */
  private void showSpell(Spell s, Object backTarget) {
    setNameAndDescription(s, false);

    appendSpellInfo(s, rootNode, myExpandableNodes, backTarget);
  }

  private void appendSpellInfo(Spell s, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes, Object backTarget) {

    if (s.getLevel() < 0 && s.getRank() < 0) {
      // no information available
      DefaultMutableTreeNode noInfo =
          createSimpleNode(Resources.get("emapdetailspanel.spell.noinfo"), "spell_noinfo");
      parent.add(noInfo);
    } else {
      // more information to tell about
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.type") + ": "
          + s.getTypeName(), "spell_type"));
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.level") + ": "
          + s.getLevel(), "spell_level"));
      parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.rank") + ": " + s.getRank(),
          "spell_rank"));

      if (s.getOnShip()) {
        // parent.add(new
        // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.spell.ship")));
        parent
            .add(createSimpleNode(Resources.get("emapdetailspanel.node.spell.ship"), "spell_ship"));
      }

      if (s.getIsFar()) {
        // parent.add(new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.spell.far")));
        parent.add(createSimpleNode(Resources.get("emapdetailspanel.node.spell.far"), "spell_far"));
      }

      if ((s.getComponents() != null) && (s.getComponents().size() > 0)) {
        // DefaultMutableTreeNode componentsNode = new
        // DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.components"));
        DefaultMutableTreeNode componentsNode =
            createSimpleNode(Resources.get("emapdetailspanel.node.components"), "things");
        parent.add(componentsNode);
        expandableNodes.add(new NodeWrapper(componentsNode,
            "EMapDetailsPanel.SpellComponentsExpanded"));

        for (String key : s.getComponents().keySet()) {
          String val = s.getComponents().get(key);
          DefaultMutableTreeNode compNode;

          // FIXME: localize?
          if (key.equalsIgnoreCase("Aura")) {
            int blankPos = val.indexOf(" ");

            if ((blankPos > 0) && (blankPos < val.length())) {
              String aura = val.substring(0, blankPos);
              String getLevelAtDays = val.substring(blankPos + 1, val.length());

              if (getLevelAtDays.equals("0")) {
                compNode =
                    createSimpleNode(aura + " " + Resources.get("emapdetailspanel.node.aura"),
                        "aura");
              } else if (getLevelAtDays.equals("1")) {
                compNode =
                    createSimpleNode(aura + " " + Resources.get("emapdetailspanel.node.aura")
                        + " * " + Resources.get("emapdetailspanel.node.level"), "aura");
              } else {
                compNode =
                    createSimpleNode(aura + " " + Resources.get("emapdetailspanel.node.aura")
                        + " * " + getLevelAtDays + " * "
                        + Resources.get("emapdetailspanel.node.level"), "aura");
              }

            } else {
              compNode = createSimpleNode(data.getTranslation(key) + ": " + val, "aura");
            }
          } else if (key.equalsIgnoreCase("permanente Aura")) {
            int blankPos = val.indexOf(" ");

            if ((blankPos > 0) && (blankPos < val.length())) {
              String aura = val.substring(0, blankPos);
              String getLevelAtDays = val.substring(blankPos + 1, val.length());

              if (getLevelAtDays.equals("0")) {
                compNode =
                    createSimpleNode(aura + " "
                        + Resources.get("emapdetailspanel.node.permanenteaura"), "permanentaura");
              } else if (getLevelAtDays.equals("1")) {
                compNode =
                    createSimpleNode(aura + " "
                        + Resources.get("emapdetailspanel.node.permanenteaura") + " * "
                        + Resources.get("emapdetailspanel.node.level"), "permanentaura");
              } else {
                compNode =
                    createSimpleNode(aura + " "
                        + Resources.get("emapdetailspanel.node.permanenteaura") + " * "
                        + getLevelAtDays + " * " + Resources.get("emapdetailspanel.node.level"),
                        "permanentaura");
              }

            } else {
              compNode = createSimpleNode(data.getTranslation(key) + ": " + val, "permanentaura");
            }

          } else {

            int blankPos = val.indexOf(" ");

            // in case of herbs the key has changed to herb.getIcon...
            // Lets see, if we have an herb here
            // or another ItemType...just in case
            String keyIcon = key;
            ItemType someItemType = data.rules.getItemType(key, false);
            if (someItemType != null && someItemType.getIconName() != null
                && someItemType.getIconName().length() > 0) {
              keyIcon = someItemType.getIconName();
            }

            if ((blankPos > 0) && (blankPos < val.length())) {
              String usage = val.substring(0, blankPos);
              String getLevelAtDays = val.substring(blankPos + 1, val.length());
              if (getLevelAtDays.equals("0")) {
                // okay, this was Resources.getOrderTranslation(key) but it doesn't make sense for
                // magic thinks (TR)
                // okay (FF), now using the correct translation
                compNode =
                    createSimpleNode(usage + " " + data.getTranslation(key), "items/" + keyIcon);
              } else if (getLevelAtDays.equals("1")) {
                // okay, this was Resources.getOrderTranslation(key) but it doesn't make sense for
                // magic thinks (TR)
                // okay (FF), now using the correct translation
                compNode =
                    createSimpleNode(usage + " " + data.getTranslation(key) + " * "
                        + Resources.get("emapdetailspanel.node.level"), "items/" + keyIcon);
              } else {
                compNode =
                    createSimpleNode(usage + " "
                        + Resources.get("emapdetailspanel.node.permanenteaura") + " * "
                        + getLevelAtDays + " * " + Resources.get("emapdetailspanel.node.level"),
                        "items/" + keyIcon);
              }
            } else {
              compNode =
                  createSimpleNode(data.getTranslation(key) + ": " + val, "items/" + keyIcon);
            }
          }
          componentsNode.add(compNode);
        }
      }

      // spellsyntax
      if (s.getSyntaxString() != null) {
        parent.add(createSimpleNode(s.getSyntaxString(), "spell_syntax"));
      }

    }
    // Backbutton
    parent.add(new DefaultMutableTreeNode(new BackButton(backTarget)));
  }

  /**
   * sets the potions name not editable sets the potions description not editable shows a tree:
   * Level : potion.level + ingredients (only if |potion.ingredients| &gt; 0) Back - ingredients:
   * 1.name ... n.name
   */
  private void showPotion(Potion p, Object backTarget) {
    setNameAndDescription(p, false);

    appendPotionInfo(p, rootNode, myExpandableNodes, backTarget);
  }

  private void appendPotionInfo(Potion p, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes, Object backTarget) {
    parent.add(new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.level") + ": "
        + p.getLevel()));

    if (p.ingredients().size() > 0) {
      DefaultMutableTreeNode ingredientsNode =
          new DefaultMutableTreeNode(Resources.get("emapdetailspanel.node.ingredients"));
      parent.add(ingredientsNode);

      for (Item ingredient : p.ingredients()) {
        ingredientsNode.add(createSimpleNode(ingredient.getItemType(), "items/"
            + ingredient.getItemType().getIconName()));
      }
    }
    parent.add(new DefaultMutableTreeNode(new BackButton(backTarget)));
  }

  /**
   * sets the island name editable sets the island description editable shows an empty tree
   */
  private void showIsland(Island i) {
    showNothing();
    setNameAndDescription(i, true);
  }

  /**
   * sets the name "" not editable sets the description "" not editable shows an empty tree
   */
  private void showNothing() {
    setNameAndDescription("", "", false);
  }

  private void storeExpansionState() {
    for (NodeWrapper nw : myExpandableNodes) {
      if (nw.getNode().getChildCount() > 0) {
        DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) nw.getNode().getFirstChild();
        settings.setProperty(nw.getDesc(), tree.isVisible(new TreePath(firstChild.getPath())) ? "1"
            : "0");
      }
    }
  }

  private void restoreExpansionState() {
    for (NodeWrapper nw : myExpandableNodes) {
      if (Integer.parseInt(settings.getProperty(nw.getDesc(), "1")) != 0) {
        if (nw.getNode().getChildCount() > 0) {
          DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) nw.getNode().getFirstChild();
          tree.makeVisible(new TreePath(firstChild.getPath()));
        }
      }
    }
  }

  /**
   * Stores the expansion state.
   * 
   * @see magellan.client.swing.InternationalizedDataPanel#quit()
   */
  @Override
  public void quit() {
    storeExpansionState();
  }

  /**
   * Selection event handler, update all elements in this panel with the appropriate data.
   * 
   * @see magellan.client.event.SelectionListener#selectionChanged(magellan.client.event.SelectionEvent)
   */
  public void selectionChanged(SelectionEvent se) {
    addTag.setEnabled(false);
    removeTag.setEnabled(false);

    /**
     * Decide whether to show a single object (activeObject) or a collection of objects
     * (selectedObjects).
     */
    Collection<?> c = se.getSelectedObjects();

    if ((c != null) && (c.size() > 1)) {
      if (showMultiple(se))
        return;
    }

    /**
     * No collection of objects could be displayd. Show the (single) active object.
     */
    if (se.isSingleSelection()) {
      show(se, true);

    } else {
      showNothing();
    }
  }

  /**
   * Tries to show the given collection. Currently, pure collections of Regions, Factions or Units
   * are implemented. If no implementation applies, false is returned, otherwise true.
   */
  protected boolean showMultiple(SelectionEvent se) {
    boolean regions = true;
    boolean factions = true;
    boolean units = true;

    setActiveSelection(null);

    for (Object o : se.getSelectedObjects()) {
      if (regions && !(o instanceof Region)) {
        regions = false;
      }

      if (factions && !(o instanceof Faction)) {
        factions = false;
      }

      if (units && !(o instanceof Unit)) {
        units = false;
      }
    }

    contextManager.setFailFallback(null, null);
    storeExpansionState();
    myExpandableNodes.clear();
    rootNode.removeAllChildren();

    if (regions) {
      setNameAndDescription(se.getSelectedObjects().size() + " Regionen", "", false);
      appendMultipleRegionInfo(CollectionFilters.uncheckedCast(se.getSelectedObjects(),
          Region.class), rootNode, myExpandableNodes);
    } else if (factions) {
      setNameAndDescription("", "", false);
      appendFactionsInfo((List<Faction>) CollectionFilters.uncheckedCast(se.getSelectedObjects(),
          Faction.class), se.getContexts(), rootNode, myExpandableNodes);
    } else if (units) {
      setNameAndDescription(se.getSelectedObjects().size() + " Einheiten", "", false);
      appendUnitsInfo(CollectionFilters.uncheckedCast(se.getSelectedObjects(), Unit.class),
          rootNode, myExpandableNodes);
    } else
      return false;

    treeModel.reload();
    restoreExpansionState();

    return true;
  }

  protected void refresh() {
    if (getActiveSelection() != null) {
      show(getActiveSelection(), false);
    }
  }

  protected SelectionEvent getActiveSelection() {
    return currentSelection;
  }

  protected void setActiveSelection(SelectionEvent se) {
    if (se == null) {
      currentSelection = null;
    } else if (se.isSingleSelection()) {
      currentSelection = se;
    } else
      throw new IllegalArgumentException("no single selection");
  }

  private Object getDisplayedObject() {
    if (currentSelection != null)
      return currentSelection.getActiveObject();
    else
      return null;
  }

  /**
   * Changes to a new object and displays its information depending on the active region.
   * 
   * @param o Report object to display
   * @param newRegion The region which contains the displayed object
   * @param dontForceRefresh If <code>true</code>, nothing is done if the new object is the already
   *          displayed object.
   */
  protected void show(SelectionEvent se, boolean dontForceRefresh) {
    try {
      if (!se.isSingleSelection())
        return;

      Object newObject = se.getSelectedObjects().iterator().next();
      List<?> newContext = se.getContexts().iterator().next();

      if (dontForceRefresh && se == getActiveSelection())
        return;

      addTag.setEnabled(false);
      removeTag.setEnabled(false);

      Object oldDisplayedObject =
          getActiveSelection() == null ? null : getActiveSelection().getActiveObject();
      setActiveSelection(se);

      if ((newObject != null) && newObject instanceof SimpleNodeWrapper) {
        newObject = ((SimpleNodeWrapper) newObject).getObject();
      }

      contextManager.setFailFallback(null, null);

      // store state, empty tree and expandableNodes
      storeExpansionState();
      myExpandableNodes.clear();
      rootNode.removeAllChildren();

      if (newObject == null) {
        showNothing();
      } else if (newObject instanceof ZeroUnit) {
        showRegion(((Unit) newObject).getRegion());
        // lastRegion = ((Unit) displayedObject).getRegion();
        contextManager.setFailFallback(newObject, commentContext);
      } else if (newObject instanceof Unit) {
        showUnit((Unit) newObject);
        // lastRegion = ((Unit) displayedObject).getRegion();
        contextManager.setFailFallback(newObject, unitCommentContext);
      } else if (newObject instanceof Region) {
        showRegion((Region) newObject);
        // lastRegion = (Region) displayedObject;
        contextManager.setFailFallback(newObject, commentContext);
      } else if (newObject instanceof Faction) {
        showFaction((Faction) newObject, newContext);
        contextManager.setFailFallback(newObject, commentContext);
      } else if (newObject instanceof Group) {
        showGroup((Group) newObject, newContext);
      } else if (newObject instanceof Building) {
        showBuilding((Building) newObject);
        // lastRegion = ((Building) displayedObject).getRegion();
        contextManager.setFailFallback(newObject, commentContext);
      } else if (newObject instanceof Ship) {
        showShip((Ship) newObject);
        // lastRegion = ((Ship) displayedObject).getRegion();
        contextManager.setFailFallback(newObject, commentContext);
      } else if (newObject instanceof Border) {
        Region r = null;
        for (Object o : newContext) {
          if (o instanceof Region) {
            r = (Region) o;
          }
        }
        showBorder((Border) newObject, r);
      } else if (newObject instanceof Spell) {
        showSpell((Spell) newObject, oldDisplayedObject);
      } else if (newObject instanceof Potion) {
        showPotion((Potion) newObject, oldDisplayedObject);
      } else if (newObject instanceof PotionNodeWrapper) {
        showPotion(((PotionNodeWrapper) newObject).getPotion(), oldDisplayedObject);
      } else if (newObject instanceof Island) {
        showIsland((Island) newObject);
      }
    } catch (Exception e) {
      // something very evil happens. Empty tree and expandableNodes
      EMapDetailsPanel.log.error("EMapDetailsPanel.show hitting on exception, args " + se + ", "
          + dontForceRefresh, e);
      myExpandableNodes.clear();
      rootNode.removeAllChildren();
    }

    // reload tree, restore state
    treeModel.reload();
    restoreExpansionState();
  }

  /**
   * handles a value change annotated by double click.
   */
  private void handleValueChange() {
    removeTag.setEnabled(false);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

    if (node == null)
      return;

    Object o = node.getUserObject();

    if (o instanceof ActionListener) {
      ((ActionListener) o).actionPerformed(null);

      return;
    }

    if (o instanceof SimpleNodeWrapper) {
      o = ((SimpleNodeWrapper) o).getObject();
    }

    Object fireObj = null;

    if (o instanceof UnitRelationNodeWrapper) {
      o = ((UnitRelationNodeWrapper) o).getInnerNode();
    }

    if (o instanceof UnitNodeWrapper) {
      fireObj = ((UnitNodeWrapper) o).getUnit();
    } else if (o instanceof UnitContainerNodeWrapper) {
      fireObj = ((UnitContainerNodeWrapper) o).getUnitContainer();
    } else if (o instanceof RegionNodeWrapper) {
      fireObj = ((RegionNodeWrapper) o).getRegion();
    } else if (o instanceof Building) {
      fireObj = o;
    } else if (o instanceof Ship) {
      fireObj = o;
    } else if (o instanceof Spell) {
      fireObj = o;
    } else if (o instanceof CombatSpell) {
      fireObj = ((CombatSpell) o).getSpell();
    } else if (o instanceof Potion) {
      fireObj = o;
    } else if (o instanceof PotionNodeWrapper) {
      fireObj = ((PotionNodeWrapper) o).getPotion();
    } else if (o instanceof Scheme) {
      fireObj = data.getRegion(((Scheme) o).getID());
    } else if (o instanceof String) {
      String s = (String) o;

      if (s.indexOf(": ") > 0) {
        TreeNode node2 = node.getParent();

        if ((node2 != null) && (node2 instanceof DefaultMutableTreeNode)) {
          Object o2 = ((DefaultMutableTreeNode) node2).getUserObject();

          if ((o2 instanceof String)
              && ((String) o2).equals(Resources.get("emapdetailspanel.node.tags"))) {
            removeTag.setEnabled(true);
          }
        }
      }
    }

    if (fireObj != null) {
      dispatcher.fire(SelectionEvent.create(this, fireObj, SelectionEvent.ST_DEFAULT));
    }
  }

  private void appendAlliances(Map<? extends ID, Alliance> allies, DefaultMutableTreeNode parent) {
    if ((allies == null) || (parent == null))
      return;

    for (Alliance alliance : allies.values()) {
      DefaultMutableTreeNode n = new DefaultMutableTreeNode(alliance.getFaction());
      parent.add(n);
      n.add(new DefaultMutableTreeNode(alliance.stateToString()));
    }
  }

  private void appendComments(UnitContainer uc, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if ((uc.getComments() != null) && (uc.getComments().size() > 0)) {
      CommentListNode commentNode =
          new CommentListNode(uc, Resources.get("emapdetailspanel.node.comments"));
      parent.add(commentNode);
      expandableNodes.add(new NodeWrapper(commentNode, "EMapDetailsPanel.CommentsExpanded"));

      int i = 0;

      for (Iterator<String> iter = uc.getComments().iterator(); iter.hasNext()
          && (i < uc.getComments().size()); i++) {
        commentNode.add(new DefaultMutableTreeNode(new UnitContainerCommentNodeWrapper(uc, iter
            .next())));
      }
    }
  }

  private void appendComments(Unit u, DefaultMutableTreeNode parent,
      Collection<NodeWrapper> expandableNodes) {
    if ((u.getComments() != null) && (u.getComments().size() > 0)) {
      UnitCommentListNode unitCommentNode =
          new UnitCommentListNode(u, Resources.get("emapdetailspanel.node.comments"));
      parent.add(unitCommentNode);
      expandableNodes
          .add(new NodeWrapper(unitCommentNode, "EMapDetailsPanel.UnitCommentsExpanded"));

      int i = 0;

      for (Iterator<String> iter = u.getComments().iterator(); iter.hasNext()
          && (i < u.getComments().size()); i++) {
        String actComment = iter.next();
        UnitCommentNodeWrapper w = new UnitCommentNodeWrapper(u, actComment);
        unitCommentNode.add(new DefaultMutableTreeNode(w));
      }
    }
  }

  /**
   * Should return all short cuts this class want to be informed. The elements should be of type
   * javax.swing.KeyStroke
   */
  public Iterator<KeyStroke> getShortCuts() {
    return shortCuts.iterator();
  }

  /**
   * This method is called when a shortcut from getShortCuts() is recognized.
   */
  public void shortCut(javax.swing.KeyStroke shortcut) {
    int index = shortCuts.indexOf(shortcut);

    switch (index) {
    case -1:
      break; // unknown shortcut

    case 0:
    case 1:
      DesktopEnvironment.requestFocus("COMMANDS");
      DesktopEnvironment.requestFocus("ORDERS");

      break;

    case 2:
      orders.setLimitMakeCompletion(!orders.getLimitMakeCompletion());

      // toggle "limit make completion"
      break;
    }
  }

  /**
   * @see magellan.client.swing.preferences.PreferencesFactory#createPreferencesAdapter()
   */
  public PreferencesAdapter createPreferencesAdapter() {
    return new DetailsViewPreferences(this);
  }

  /**
   * Called when one of the tag buttons is pressed.
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
    if (actionEvent.getSource() == addTag) {
      String key =
          JOptionPane.showInputDialog(tree, Resources
              .get("emapdetailspanel.addtag.tagname.message"));

      if ((key != null) && (key.length() > 0)) {
        String value =
            JOptionPane.showInputDialog(tree, Resources
                .get("emapdetailspanel.addtag.tagvalue.message"));

        if ((value != null) && (value.length() > 0)) {
          if (getDisplayedObject() instanceof UnitContainer) {
            ((UnitContainer) getDisplayedObject()).putTag(key, value);
          } else if (getDisplayedObject() instanceof Unit) {
            Unit u = (Unit) getDisplayedObject();
            u.putTag(key, value);
            dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, u));
          }

          refresh();

        }
      }
    } else {
      Object o = tree.getLastSelectedPathComponent();

      if (o instanceof DefaultMutableTreeNode) {
        Object o2 = ((DefaultMutableTreeNode) o).getUserObject();

        if (o2 instanceof String) {
          int index = ((String) o2).indexOf(": ");

          if (index > 0) {
            String tagName = ((String) o2).substring(0, index);
            EMapDetailsPanel.log.info("Removing " + tagName);

            if (getDisplayedObject() instanceof UnitContainer) {
              ((UnitContainer) getDisplayedObject()).removeTag(tagName);
              refresh();
            } else if (getDisplayedObject() instanceof Unit) {
              Unit u = (Unit) getDisplayedObject();
              u.removeTag(tagName);
              refresh();

              dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, u));
            }
          }
        }
      }
    }
  }

  /**
	 */
  public boolean isShowingTagButtons() {
    return showTagButtons;
  }

  public boolean isAllowingCustomIcons() {
    return allowCustomIcons;
  }

  public boolean isCompactLayout() {
    return compactLayout;
  }

  /**
   * Called to activate or deactivate the buttons for adding tags.
   */
  public void setShowTagButtons(boolean bool) {
    if (bool != isShowingTagButtons()) {
      showTagButtons = bool;
      settings.setProperty("EMapDetailsPanel.ShowTagButtons", bool ? "true" : "false");

      if (bool) {
        treeContainer.add(tagContainer, BorderLayout.SOUTH);
        tagContainer.doLayout();
      } else {
        treeContainer.remove(tagContainer);
      }

      treeContainer.doLayout();
      treeContainer.validate();
      treeContainer.repaint();
    }
  }

  public void setAllowCustomIcons(boolean bool) {
    if (bool != isAllowingCustomIcons()) {
      allowCustomIcons = bool;
      settings.setProperty("EMapDetailsPanel.AllowCustomIcons", bool ? "true" : "false");
      treeContainer.doLayout();
      treeContainer.validate();
      treeContainer.repaint();
    }
  }

  public void setCompactLayout(boolean selected) {
    if (selected != isCompactLayout()) {
      compactLayout = selected;
      settings.setProperty("EMapDetailsPanel.CompactLayout", selected ? "true" : "false");
    }
  }

  /**
   * Repaint the tree.
   */
  public void updateTree(Object src) {
    tree.treeDidChange();

    /*
     * if (tree.getModel() instanceof DefaultTreeModel) {
     * ((DefaultTreeModel)tree.getModel()).reload(); }
     */

    // Use the UI Fix
    javax.swing.plaf.TreeUI treeUI = tree.getUI();

    if (treeUI instanceof javax.swing.plaf.basic.BasicTreeUI) {
      javax.swing.plaf.basic.BasicTreeUI ui2 = (javax.swing.plaf.basic.BasicTreeUI) treeUI;
      int i = ui2.getLeftChildIndent();
      ui2.setLeftChildIndent(100);
      ui2.setLeftChildIndent(i);
    }

    tree.revalidate();
    tree.repaint();
  }

  /**
   * @see magellan.client.desktop.ShortcutListener#getShortcutDescription(KeyStroke)
   */
  public String getShortcutDescription(KeyStroke stroke) {
    int index = shortCuts.indexOf(stroke);

    return Resources.get("emapdetailspanel.shortcuts.description." + String.valueOf(index));
  }

  /**
   * @see magellan.client.desktop.ShortcutListener#getListenerDescription()
   */
  public String getListenerDescription() {
    return Resources.get("emapdetailspanel.shortcuts.title");
  }

  /**
   * A NodeWrapper for category nodes with a description, used for storing expansion states
   */
  private class NodeWrapper {
    private DefaultMutableTreeNode node = null;
    private String desc = "";

    /**
     * Creates a new NodeWrapper object.
     */
    public NodeWrapper(DefaultMutableTreeNode n, String d) {
      node = n;
      desc = d;
    }

    public DefaultMutableTreeNode getNode() {
      return node;
    }

    public String getDesc() {
      return desc;
    }
  }

  private class SkillStatItem {
    public Skill skill = null;

    public int unitCounter = 0;

    public List<Unit> units = new LinkedList<Unit>();

    /**
     * Creates a new SkillStatItem object.
     */
    public SkillStatItem(Skill skill, int unitCounter) {
      this.skill = skill;
      this.unitCounter = unitCounter;
    }
  }

  private class SkillStatItemComparator implements Comparator<SkillStatItem> {
    /**
     * Compare by skill level.
     */
    public int compare(SkillStatItem o1, SkillStatItem o2) {
      int retVal = o1.skill.getName().compareTo(o2.skill.getName());

      if (retVal == 0) {
        retVal = o2.skill.getLevel() - o1.skill.getLevel();
      }

      return retVal;
    }

  }

  /**
   * A class that can be used to interpret a tree entry as a button
   */
  private abstract class SimpleActionObject implements CellObject, ActionListener {
    protected List<String> icons;
    private final String key;

    SimpleActionObject(String key) {
      this.key = key;
    }

    /**
		 */
    @Override
    public String toString() {
      return Resources.get("emapdetailspanel." + key);
    }

    /**
		 */
    public boolean emphasized() {
      return false;
    }

    /**
		 */
    public List<String> getIconNames() {
      return icons;
    }

    /**
		 */
    public void propertiesChanged() {
    }

    /**
		 */
    public NodeWrapperDrawPolicy init(Properties settings, String prefix,
        NodeWrapperDrawPolicy adapter) {
      return null;
    }

    /**
		 */
    public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
      return null;
    }
  }

  /**
   * A class to recognize a back-button in the tree
   */
  private class BackButton extends SimpleActionObject {
    private Object target;

    BackButton(Object t) {
      super("backbutton.text");
      icons = new ArrayList<String>(1);
      icons.add("zurueck");
      target = t;
    }

    /**
		 */
    @SuppressWarnings("unused")
    public Object getTarget() {
      return target;
    }

    /**
		 */
    public void actionPerformed(ActionEvent e) {
      dispatcher.fire(SelectionEvent.create(EMapDetailsPanel.this, target,
          SelectionEvent.ST_DEFAULT));
    }
  }

  /**
   * Ship routing planer button
   */
  private class ShipRoutingPlannerButton extends SimpleActionObject {
    private Ship target;

    ShipRoutingPlannerButton(Ship s) {
      super("shipplaner.text");
      target = s;
      icons = new ArrayList<String>(1);
      icons.add("koordinaten");
    }

    /**
		 */
    public void actionPerformed(ActionEvent e) {
      Unit unit =
          (new ShipRoutePlanner()).planShipRoute(target, data, EMapDetailsPanel.this,
              new RoutingDialog(JOptionPane.getFrameForComponent(EMapDetailsPanel.this), data,
                  false));

      if (unit != null) {
        dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, unit));
      }
    }
  }

  private class StealthContextFactory implements ContextFactory {
    /**
     * @see magellan.client.swing.context.ContextFactory#createContextMenu(EventDispatcher,
     *      GameData, Object, SelectionEvent, DefaultMutableTreeNode)
     */
    public javax.swing.JPopupMenu createContextMenu(EventDispatcher dispatcher, GameData data,
        Object argument, SelectionEvent selectedObjects, DefaultMutableTreeNode node) {
      if (argument instanceof Unit) {
        try {
          return new StealthContextMenu((Unit) argument);
        } catch (IllegalArgumentException exc) {
        }
      }

      return null;
    }

    private class StealthContextMenu extends JPopupMenu implements ActionListener {
      private Unit unit;

      /**
       * Creates a new StealthContextMenu object.
       * 
       * @throws IllegalArgumentException if unit's stealth level is 0.
       */
      public StealthContextMenu(Unit u) {
        unit = u;

        Skill stealth = unit.getSkill(data.rules.getSkillType(EresseaConstants.S_TARNUNG, true));

        if (stealth.getLevel() > 0) {
          for (int i = 0; i <= stealth.getLevel(); i++) {
            JMenuItem item = new JMenuItem(String.valueOf(i));
            item.addActionListener(this);
            this.add(item);
          }
        } else
          throw new IllegalArgumentException("Unit has no stealth capability.");
      }

      /**
       * Adds a hide order.
       */
      public void actionPerformed(ActionEvent e) {
        int newStealth = Integer.parseInt(e.getActionCommand());

        if (newStealth != unit.getStealth()) {
          unit.setStealth(newStealth);

          refresh();
          data.getGameSpecificStuff().getOrderChanger().addHideOrder(unit,
              e.getActionCommand().toString());

          /*
           * Note: Of course it would be better to inform all that a game data object has changed
           * but I think it's not necessary and consumes too much time Andreas
           */
          dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, unit));
        }
      }
    }
  }

  private class CombatStateContextFactory implements ContextFactory {
    private class CombatStateContextMenu extends JPopupMenu implements ActionListener {
      private Unit unit;

      /**
       * Creates a new CombatStateContextMenu object.
       */
      public CombatStateContextMenu(Unit u) {
        unit = u;
        init();
      }

      protected void init() {
        for (int i = 0; i < 6; i++) {
          addItem(i);
        }
      }

      protected void addItem(int state) {
        JMenuItem item = new JMenuItem(MagellanFactory.combatStatusToString(state));
        item.setActionCommand(String.valueOf(state));
        item.addActionListener(this);
        this.add(item);
      }

      /**
       * Adds an order for new combat status.
       */
      public void actionPerformed(ActionEvent e) {
        int newState = Integer.parseInt(e.getActionCommand());

        if (newState != unit.getCombatStatus()) {
          unit.setCombatStatus(newState);

          refresh();
          data.getGameSpecificStuff().getOrderChanger().addCombatOrder(unit, newState);

          // Note: Same as in StealthContextMenu
          // dispatcher.fire(new GameDataEvent(EMapDetailsPanel.this, data));
          dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, unit));
        }
      }
    }

    /**
     * @see magellan.client.swing.context.ContextFactory#createContextMenu(EventDispatcher,
     *      GameData, Object, SelectionEvent, DefaultMutableTreeNode)
     */
    public javax.swing.JPopupMenu createContextMenu(EventDispatcher dispatcher, GameData data,
        Object argument, SelectionEvent selectedObjects, DefaultMutableTreeNode node) {
      if (argument instanceof Unit)
        return new CombatStateContextMenu((Unit) argument);

      return null;
    }
  }

  /**
   * Creates context menus for {@link UnitRelationNodeWrapper} and {@link UnitRelationNodeWrapper2}.
   */
  private class RelationContextFactory implements ContextFactory {
    /**
     * @see magellan.client.swing.context.ContextFactory#createContextMenu(EventDispatcher,
     *      GameData, Object, SelectionEvent, DefaultMutableTreeNode)
     */
    public javax.swing.JPopupMenu createContextMenu(EventDispatcher dispatcher, GameData data,
        Object argument, SelectionEvent selectedObjects, DefaultMutableTreeNode node) {
      try {
        if (argument instanceof UnitRelationNodeWrapper)
          return new RelationContextMenu(((UnitRelationNodeWrapper) argument).getArgument());
        else
          return null;
      } catch (IllegalArgumentException exc) {
      }

      return null;
    }

    /**
     * Context menu for unit relation nodes.
     */
    private class RelationContextMenu extends UnitContextMenu implements ActionListener {

      /**
       * Action for changing an order
       */
      public abstract class SetAction extends AbstractAction {
        protected UnitRelation relation;

        public SetAction(UnitRelation r, String title) {
          super(title);
          relation = r;
        }

        public void replace(String newOrder) {
          relation.origin.removeOrderAt(relation.line - 1, false);
          if (newOrder != null) {
            relation.origin.addOrderAt(relation.line - 1, newOrder, false);
          }
          relation.origin.refreshRelations();
          dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, relation.origin));
        }
      }

      /**
       * Action for changing a reserve order
       */
      public abstract class SetReserveAction extends SetAction {
        protected ReserveRelation reserveRelation;

        public SetReserveAction(ReserveRelation r, String title) {
          super(r, title);
          reserveRelation = r;
        }

        public String getOrder(String amount, boolean each) {
          Locale locale = Locales.getOrderLocale();
          // relation.origin.getFaction() != null ? relation.origin.getFaction().getLocale() :

          StringBuffer reserve =
              new StringBuffer(Resources.getOrderTranslation(EresseaConstants.O_RESERVE, locale));
          reserve.append(" ");
          if (each) {
            reserve.append(Resources.getOrderTranslation(EresseaConstants.O_EACH, locale));
            reserve.append(" ");
          }
          reserve.append(amount);
          reserve.append(" ");
          reserve.append(reserveRelation.itemType);
          return reserve.toString();
        }
      }

      /**
       * Action for setting reserve order to 0.
       */
      public class Reserve0Action extends SetReserveAction {
        public Reserve0Action(ReserveRelation r) {
          super(r, Resources.get("emapdetailspanel.contextmenu.reserve.set0.title"));
        }

        public void actionPerformed(ActionEvent e) {
          replace(null);
        }
      }

      /**
       * Action for setting reserve order to some amount.
       */
      public class ReserveNumberAction extends SetReserveAction {
        private boolean each;

        public ReserveNumberAction(ReserveRelation r, boolean each) {
          super(r, Resources.get("emapdetailspanel.contextmenu.reserve.set." + each + ".title"));
          this.each = each;
        }

        public void actionPerformed(ActionEvent e) {
          String result =
              JOptionPane.showInputDialog(RelationContextMenu.this, getOrder("...", each),
                  Resources
                      .get("emapdetailspanel.contextmenu.reserve.getamount." + each + ".title"),
                  JOptionPane.QUESTION_MESSAGE);
          if (result != null) {
            try {
              Integer.parseInt(result);
              replace(getOrder(result, each));
            } catch (NumberFormatException exc) {

            }
          }
        }
      }

      /**
       * Action for setting a give order.
       */
      public abstract class SetGiveAction extends SetAction {
        private ItemTransferRelation transferRelation;

        public SetGiveAction(ItemTransferRelation r, String title) {
          super(r, title);
          transferRelation = r;
        }

        public String getOrder(String amount, boolean each) {
          Locale locale = Locales.getOrderLocale();
          // relation.origin.getFaction() != null ? relation.origin.getFaction().getLocale() :

          StringBuffer reserve =
              new StringBuffer(Resources.getOrderTranslation(EresseaConstants.O_GIVE, locale));
          reserve.append(" ");
          reserve.append(transferRelation.target.getID());
          reserve.append(" ");
          if (each) {
            reserve.append(Resources.getOrderTranslation(EresseaConstants.O_EACH, locale));
            reserve.append(" ");
          }
          reserve.append(amount);
          reserve.append(" ");
          reserve.append(transferRelation.itemType);
          return reserve.toString();
        }
      }

      /**
       * Action for setting a give order to 0.
       */
      public class Give0Action extends SetGiveAction {
        public Give0Action(ItemTransferRelation r) {
          super(r, Resources.get("emapdetailspanel.contextmenu.give.set0.title"));
        }

        public void actionPerformed(ActionEvent e) {
          replace(null);
        }
      }

      /**
       * Action for setting a give order to some amount.
       */
      public class GiveNumberAction extends SetGiveAction {
        private boolean each;

        public GiveNumberAction(ItemTransferRelation r, boolean each) {
          super(r, Resources.get("emapdetailspanel.contextmenu.give.set." + each + ".title"));
          this.each = each;
        }

        public void actionPerformed(ActionEvent e) {
          String result =
              JOptionPane.showInputDialog(RelationContextMenu.this, getOrder("...", each),
                  Resources.get("emapdetailspanel.contextmenu.give.getamount." + each + ".title"),
                  JOptionPane.QUESTION_MESSAGE);
          if (result != null) {
            try {
              Integer.parseInt(result);
              replace(getOrder(result, each));
            } catch (NumberFormatException exc) {

            }
          }
        }
      }

      /**
       * Creates a new ReserveContextMenu object.
       */
      public RelationContextMenu(Object r) {
        super(((UnitRelation) r).origin, null, dispatcher, data);
        this.add(new JSeparator());
        if (r instanceof ReserveRelation) {
          JMenuItem item = new JMenuItem(new Reserve0Action((ReserveRelation) r));
          item.addActionListener(this);
          this.add(item);
          item = new JMenuItem(new ReserveNumberAction((ReserveRelation) r, false));
          item.addActionListener(this);
          this.add(item);
          item = new JMenuItem(new ReserveNumberAction((ReserveRelation) r, true));
          item.addActionListener(this);
          this.add(item);
        } else if (r instanceof ItemTransferRelation) {
          JMenuItem item = new JMenuItem(new Give0Action((ItemTransferRelation) r));
          item.addActionListener(this);
          this.add(item);
          item = new JMenuItem(new GiveNumberAction((ItemTransferRelation) r, false));
          item.addActionListener(this);
          this.add(item);
          item = new JMenuItem(new GiveNumberAction((ItemTransferRelation) r, true));
          item.addActionListener(this);
          this.add(item);
        }
      }

      public void actionPerformed(ActionEvent e) {
      }

    }
  }

  /** make comment nodes recognizable through this class */
  private class CommentNode extends DefaultMutableTreeNode {
    private UnitContainer uc = null;

    /**
     * Creates a new CommentNode object.
     */
    public CommentNode(UnitContainer uc, String comment) {
      super(comment);
      this.uc = uc;
    }

    /**
		 */
    public UnitContainer getUnitContainer() {
      return uc;
    }
  }

  private class CommentListNode extends CommentNode {
    /**
     * Creates a new CommentListNode object.
     */
    public CommentListNode(UnitContainer uc, String title) {
      super(uc, title);
    }
  }

  // make unit comment nodes recognizable through this class
  private class UnitCommentNode extends DefaultMutableTreeNode {
    private Unit u = null;

    /**
     * Creates a new CommentNode object.
     */
    public UnitCommentNode(Unit u, String comment) {
      super(comment);
      this.u = u;
    }

    /**
		 */
    public Unit getUnit() {
      return u;
    }
  }

  private class UnitCommentListNode extends UnitCommentNode {
    /**
     * Creates a new CommentListNode object.
     */
    public UnitCommentListNode(Unit u, String title) {
      super(u, title);
    }
  }

  private class CommentContextFactory implements ContextFactory {
    /**
		 */
    public JPopupMenu createContextMenu(EventDispatcher dispatcher, GameData data, Object argument,
        SelectionEvent selectedObjects, DefaultMutableTreeNode node) {
      if (argument instanceof UnitContainer)
        return new CommentContextMenu((UnitContainer) argument, node, false);
      else if (argument instanceof CommentListNode)
        return new CommentContextMenu(((CommentListNode) argument).getUnitContainer(), node, true);
      else if (argument instanceof CommentNode)
        return new CommentContextMenu(((CommentNode) argument).getUnitContainer(), node, false);
      else if (argument instanceof UnitContainerCommentNodeWrapper)
        return new CommentContextMenu(((UnitContainerCommentNodeWrapper) argument)
            .getUnitContainer(), node, false);

      return null;
    }

    private class CommentContextMenu extends JPopupMenu implements ActionListener {
      private final Logger contextLog = Logger.getInstance(CommentContextMenu.class);
      private UnitContainer uc = null;
      private DefaultMutableTreeNode node = null;
      private UnitContainerCommentNodeWrapper nodeWrapper = null;
      private JMenuItem addComment;
      private JMenuItem removeComment;
      private JMenuItem modifyComment;
      private JMenuItem removeAllComments;

      /**
       * Creates a new CommentContextMenu object.
       */
      public CommentContextMenu(UnitContainer container, DefaultMutableTreeNode node,
          boolean removeAll) {
        uc = container;

        this.node = node;
        Object o = null;
        if (node != null) {
          o = node.getUserObject();
          if (o instanceof UnitContainerCommentNodeWrapper) {
            nodeWrapper = (UnitContainerCommentNodeWrapper) o;
          }
        }

        addComment = new JMenuItem(Resources.get("emapdetailspanel.menu.createcomment"));
        addComment.addActionListener(this);
        this.add(addComment);

        if (node != null && o != null) {
          if (o instanceof UnitContainerCommentNodeWrapper) {
            modifyComment = new JMenuItem(Resources.get("emapdetailspanel.menu.changecomment"));
            modifyComment.addActionListener(this);
            removeComment = new JMenuItem(Resources.get("emapdetailspanel.menu.removecomment"));
            removeComment.addActionListener(this);

            this.add(modifyComment);
            this.add(removeComment);
          }
        }

        if (removeAll) {
          removeAllComments =
              new JMenuItem(Resources.get("emapdetailspanel.menu.removecomment.all"));
          removeAllComments.addActionListener(this);
          this.add(removeAllComments);
        }
      }

      private void addComment() {
        DefaultMutableTreeNode parent = null;

        for (Enumeration<?> en = rootNode.children(); en.hasMoreElements();) {
          DefaultMutableTreeNode n = (DefaultMutableTreeNode) en.nextElement();
          Object obj = n.getUserObject();

          if ((obj != null) && obj instanceof String
              && obj.equals(Resources.get("emapdetailspanel.node.comments"))) {
            parent = n;

            break;
          }
        }

        if (parent == null) {
          parent = new CommentListNode(uc, Resources.get("emapdetailspanel.node.comments"));
          rootNode.add(parent);
        }

        if (uc.getComments() == null) {
          uc.setComments(new LinkedList<String>());
        }

        if (uc.getComments().size() != parent.getChildCount()) {
          contextLog
              .info("EMapDetailsPanel.DetailsContextMenu.getCreateCommentMenuItem(): number of comments and nodes differs!");

          return;
        }

        String newComment = uc.toString();
        uc.getComments().add(newComment);

        DefaultMutableTreeNode newNode =
            new DefaultMutableTreeNode(new UnitContainerCommentNodeWrapper(uc, newComment));
        parent.add(newNode);
        treeModel.reload();
        restoreExpansionState();
        tree.startEditingAtPath(new TreePath(newNode.getPath()));
        storeExpansionState();
      }

      private void modifyComment() {
        if ((nodeWrapper.getUnitContainer() != null)
            && (nodeWrapper.getUnitContainer().getComments() != null)) {
          tree.startEditingAtPath(new TreePath(node.getPath()));
        }
      }

      private void removeComment() {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

        if (uc.getComments() != null) {
          if (uc.getComments().size() == parent.getChildCount()) {
            uc.getComments().remove(parent.getIndex(node));
          } else {
            contextLog
                .info("EMapDetailsPanel.DetailsContextMenu.getDeleteCommentMenuItem(): number of comments and nodes differs!");

            return;
          }
        }

        treeModel.removeNodeFromParent(node);

        if (parent.getChildCount() == 0) {
          treeModel.removeNodeFromParent(parent);
        }

        treeModel.reload();
        restoreExpansionState();
      }

      private void removeAllComments() {
        uc.getComments().clear();
        uc.setComments(null);
        refresh();
      }

      /**
       * Invoked when an action occurs.
       */
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addComment) {
          addComment();
        } else if (e.getSource() == removeComment) {
          removeComment();
        } else if (e.getSource() == modifyComment) {
          modifyComment();
        } else if (e.getSource() == removeAllComments) {
          removeAllComments();
        }
      }
    }
  }

  private class UnitCommentContextFactory implements ContextFactory {
    /**
     * @see magellan.client.swing.context.ContextFactory#createContextMenu(EventDispatcher,
     *      GameData, Object, SelectionEvent, DefaultMutableTreeNode)
     */
    public JPopupMenu createContextMenu(EventDispatcher dispatcher, GameData data, Object argument,
        SelectionEvent selectedObjects, DefaultMutableTreeNode node) {
      if (argument instanceof Unit)
        return new UnitCommentContextMenu((Unit) argument, node, false);
      else if (argument instanceof UnitCommentListNode)
        return new UnitCommentContextMenu(((UnitCommentListNode) argument).getUnit(), node, true);
      else if (argument instanceof UnitCommentNodeWrapper)
        return new UnitCommentContextMenu(((UnitCommentNodeWrapper) argument).getUnit(), node,
            false);

      return null;
    }

    private class UnitCommentContextMenu extends JPopupMenu implements ActionListener {
      private final Logger contextLog = Logger.getInstance(UnitCommentContextMenu.class);
      private Unit u = null;
      private DefaultMutableTreeNode node = null;
      private UnitCommentNodeWrapper nodeWrapper = null;
      private JMenuItem addComment;
      private JMenuItem removeComment;
      private JMenuItem modifyComment;
      private JMenuItem removeAllComments;

      /**
       * Creates a new CommentContextMenu object.
       */
      public UnitCommentContextMenu(Unit u, DefaultMutableTreeNode node, boolean removeAll) {

        this.u = u;
        this.node = node;
        Object o = null;
        if (node != null) {
          o = node.getUserObject();
          if (o instanceof UnitCommentNodeWrapper) {
            nodeWrapper = (UnitCommentNodeWrapper) o;
          }
        }

        addComment = new JMenuItem(Resources.get("emapdetailspanel.menu.createcomment"));
        addComment.addActionListener(this);
        this.add(addComment);

        if (node != null && o != null) {
          if (o instanceof UnitCommentNodeWrapper) {
            modifyComment = new JMenuItem(Resources.get("emapdetailspanel.menu.changecomment"));
            modifyComment.addActionListener(this);
            removeComment = new JMenuItem(Resources.get("emapdetailspanel.menu.removecomment"));
            removeComment.addActionListener(this);

            this.add(modifyComment);
            this.add(removeComment);
          }
        }

        if (removeAll) {
          removeAllComments =
              new JMenuItem(Resources.get("emapdetailspanel.menu.removecomment.all"));
          removeAllComments.addActionListener(this);
          this.add(removeAllComments);
        }
      }

      private void addComment() {
        DefaultMutableTreeNode parent = null;

        for (Enumeration<?> en = rootNode.children(); en.hasMoreElements();) {
          DefaultMutableTreeNode n = (DefaultMutableTreeNode) en.nextElement();
          Object obj = n.getUserObject();

          if ((obj != null) && obj instanceof String
              && obj.equals(Resources.get("emapdetailspanel.node.comments"))) {
            parent = n;

            break;
          }
        }

        if (parent == null) {
          parent = new UnitCommentListNode(u, Resources.get("emapdetailspanel.node.comments"));
          rootNode.add(parent);
        }

        if (u.getComments() == null) {
          u.setComments(new LinkedList<String>());
        }

        if (u.getComments().size() != parent.getChildCount()) {
          contextLog
              .info("EMapDetailsPanel.DetailsContextMenu.getCreateCommentMenuItem(): number of comments and nodes differs!");

          return;
        }

        String newComment = u.toString();
        u.getComments().add(newComment);

        DefaultMutableTreeNode newNode =
            new DefaultMutableTreeNode(new UnitCommentNodeWrapper(u, newComment));
        parent.add(newNode);
        treeModel.reload();
        restoreExpansionState();
        tree.startEditingAtPath(new TreePath(newNode.getPath()));
        storeExpansionState();
      }

      private void modifyComment() {
        if ((nodeWrapper.getUnit() != null) && (nodeWrapper.getUnit().getComments() != null)) {
          tree.startEditingAtPath(new TreePath(node.getPath()));
        }
      }

      private void removeComment() {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

        if (u.getComments() != null) {
          if (u.getComments().size() == parent.getChildCount()) {
            u.getComments().remove(parent.getIndex(node));
          } else {
            contextLog
                .info("EMapDetailsPanel.DetailsUnitContextMenu.getDeleteCommentMenuItem(): number of comments and nodes differs!");
            return;
          }
        }

        treeModel.removeNodeFromParent(node);

        if (parent.getChildCount() == 0) {
          treeModel.removeNodeFromParent(parent);
        }

        treeModel.reload();
        restoreExpansionState();
      }

      private void removeAllComments() {
        u.getComments().clear();
        u.setComments(null);
        refresh();
      }

      /**
       * Invoked when an action occurs.
       */
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addComment) {
          addComment();
        } else if (e.getSource() == removeComment) {
          removeComment();
        } else if (e.getSource() == modifyComment) {
          modifyComment();
        } else if (e.getSource() == removeAllComments) {
          removeAllComments();
        }
      }
    }
  }

  /**
   * We need a special factory for the normal UnitContextMenu since we have to take care of unit
   * lists.
   */
  private class DetailsUnitContextFactory implements ContextFactory {

    /**
     * @see magellan.client.swing.context.ContextFactory#createContextMenu(EventDispatcher,
     *      GameData, Object, SelectionEvent, DefaultMutableTreeNode)
     */
    public JPopupMenu createContextMenu(EventDispatcher dispatcher, GameData data, Object argument,
        SelectionEvent selectedObjects, DefaultMutableTreeNode node) {

      if (argument instanceof Unit)
        return new UnitContextMenu((Unit) argument, selectedObjects.getSelectedObjects(),
            dispatcher, data);
      else if (argument instanceof UnitNodeWrapper)
        return new UnitContextMenu(((UnitNodeWrapper) argument).getUnit(), selectedObjects
            .getSelectedObjects(), dispatcher, data);
      else if (argument instanceof UnitListNodeWrapper) {
        Collection<Unit> col = ((UnitListNodeWrapper) argument).getUnits();

        if ((col != null) && (col.size() > 0))
          return new UnitContextMenu(col.iterator().next(), col, dispatcher, data);
      } else if (argument instanceof DefaultMutableTreeNode) {
        DefaultMutableTreeNode actArg = (DefaultMutableTreeNode) argument;
        Object actUserObject = actArg.getUserObject();
        if (actUserObject instanceof SimpleNodeWrapper) {
          SimpleNodeWrapper actSNW = (SimpleNodeWrapper) actUserObject;
          if (actSNW.toString().toLowerCase().startsWith(
              Resources.get("emapdetailspanel.node.capacityonfoot").toLowerCase())
              || actSNW.toString().toLowerCase().startsWith(
                  Resources.get("emapdetailspanel.node.capacityonhorse").toLowerCase()))
            return new UnitCapacityContextMenu(dispatcher, data, settings);
        }
      } else {

      }

      return null;
    }
  }

  /**
   * 
   */
  private class RaceInfo {
    int amount = 0;
    int amount_modified = 0;
    String raceNoPrefix = null;
  }

  /**
   * @see magellan.client.swing.MenuProvider#getMenu()
   */
  public JMenu getMenu() {
    JMenu tree = new JMenu(Resources.get("emapdetailspanel.menu.caption"));
    tree.setMnemonic(Resources.get("emapdetailspanel.menu.mnemonic").charAt(0));
    tree.add(nodeWrapperFactory.getContextMenu());

    return tree;
  }

  /**
   * @see magellan.client.swing.MenuProvider#getSuperMenu()
   */
  public String getSuperMenu() {
    return "tree";
  }

  /**
   * @see magellan.client.swing.MenuProvider#getSuperMenuTitle()
   */
  public String getSuperMenuTitle() {
    return Resources.get("emapdetailspanel.menu.supertitle");
  }

  /**
   * Returns the Order Editor component.
   */
  public MultiEditorOrderEditorList getEditor() {
    return editor;
  }

  /**
   * Returns the AutoCompletion Editor component.
   */
  public AutoCompletion getOrders() {
    return orders;
  }

  /**
   * Returns the Region Panel.
   */
  public BasicRegionPanel getRegionPanel() {
    return regionPanel;
  }

  /**
   * outsourced handling of changes in tree-selections updating mySelectedUnits
   * 
   * @author Fiete
   * @param tslE the TreeSelectionEvent from The Listener of our Tree
   */
  private void handleTreeSelectionChangeEvent(TreeSelectionEvent tslE) {
    List<Unit> mySelectedUnitss = new LinkedList<Unit>();
    TreePath paths[] = tree.getSelectionPaths();
    Unit activeUnit = null;
    if (paths != null) {
      for (TreePath path : paths) {
        DefaultMutableTreeNode actNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object o = actNode.getUserObject();
        if (o instanceof UnitNodeWrapper) {
          UnitNodeWrapper unitNodeWrapper = (UnitNodeWrapper) o;
          Unit actUnit = unitNodeWrapper.getUnit();
          mySelectedUnitss.add(actUnit);
        }
      }

      DefaultMutableTreeNode actNode =
          (DefaultMutableTreeNode) tslE.getNewLeadSelectionPath().getLastPathComponent();
      Object o = actNode.getUserObject();
      if (o instanceof UnitNodeWrapper) {
        UnitNodeWrapper unitNodeWrapper = (UnitNodeWrapper) o;
        activeUnit = unitNodeWrapper.getUnit();
      }
    }
    if (mySelectedUnitss.size() > 0) {
      contextManager.setSelection(SelectionEvent.create(EMapDetailsPanel.this, activeUnit,
          mySelectedUnitss));
    } else {
      contextManager.setSelection(null);
    }
  }

  // /**
  // * Sets the value of activeRegion.
  // *
  // * @param activeRegion The value for activeRegion.
  // */
  // protected void setActiveRegion(Region activeRegion) {
  // this.activeRegion = activeRegion;
  // }

  // /**
  // * Returns the value of activeRegion.
  // *
  // * @return Returns activeRegion.
  // */
  // protected Region getActiveRegion() {
  // return activeRegion;
  // }

}
