/*
 * Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe, Stefan Goetz, Sebastian Pappert, Klaas
 * Prause, Enno Rehling, Sebastian Tusk, Ulrich Kuester, Ilja Pavkovic This file is part of the
 * Eressea Java Code Base, see the file LICENSING for the licensing information applying to this
 * file.
 */

package magellan.client;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;

import magellan.client.actions.MenuAction;
import magellan.client.actions.edit.FindAction;
import magellan.client.actions.edit.QuickFindAction;
import magellan.client.actions.edit.RedoAction;
import magellan.client.actions.edit.UndoAction;
import magellan.client.actions.extras.AlchemyAction;
import magellan.client.actions.extras.ArmyStatsAction;
import magellan.client.actions.extras.ConversionAction;
import magellan.client.actions.extras.FactionStatsAction;
import magellan.client.actions.extras.HelpAction;
import magellan.client.actions.extras.InfoAction;
import magellan.client.actions.extras.OptionAction;
import magellan.client.actions.extras.ProfileAction;
import magellan.client.actions.extras.RepaintAction;
import magellan.client.actions.extras.TileSetAction;
import magellan.client.actions.extras.TipOfTheDayAction;
import magellan.client.actions.extras.TradeOrganizerAction;
import magellan.client.actions.extras.VorlageAction;
import magellan.client.actions.file.AbortAction;
import magellan.client.actions.file.AddCRAction;
import magellan.client.actions.file.ExportCRAction;
import magellan.client.actions.file.FileSaveAction;
import magellan.client.actions.file.FileSaveAsAction;
import magellan.client.actions.file.OpenCRAction;
import magellan.client.actions.file.OpenOrdersAction;
import magellan.client.actions.file.QuitAction;
import magellan.client.actions.file.SaveOrdersAction;
import magellan.client.actions.map.AddSelectionAction;
import magellan.client.actions.map.ExpandSelectionAction;
import magellan.client.actions.map.FillSelectionAction;
import magellan.client.actions.map.InvertSelectionAction;
import magellan.client.actions.map.IslandAction;
import magellan.client.actions.map.MapSaveAction;
import magellan.client.actions.map.OpenSelectionAction;
import magellan.client.actions.map.SaveSelectionAction;
import magellan.client.actions.map.SelectAllAction;
import magellan.client.actions.map.SelectIslandsAction;
import magellan.client.actions.map.SelectNothingAction;
import magellan.client.actions.map.SetGirthAction;
import magellan.client.actions.map.SetOriginAction;
import magellan.client.actions.orders.ChangeFactionConfirmationAction;
import magellan.client.actions.orders.ConfirmAction;
import magellan.client.actions.orders.FindPreviousUnconfirmedAction;
import magellan.client.actions.orders.UnconfirmAction;
import magellan.client.desktop.DesktopEnvironment;
import magellan.client.desktop.MagellanDesktop;
import magellan.client.desktop.ShortcutListener;
import magellan.client.event.EventDispatcher;
import magellan.client.event.OrderConfirmEvent;
import magellan.client.event.OrderConfirmListener;
import magellan.client.event.SelectionEvent;
import magellan.client.event.SelectionListener;
import magellan.client.event.TempUnitEvent;
import magellan.client.event.TempUnitListener;
import magellan.client.event.UnitOrdersEvent;
import magellan.client.event.UnitOrdersListener;
import magellan.client.extern.MagellanPlugIn;
import magellan.client.extern.MagellanPlugInLoader;
import magellan.client.extern.MainMenuProvider;
import magellan.client.preferences.ClientPreferences;
import magellan.client.swing.AskForPasswordDialog;
import magellan.client.swing.DebugDock;
import magellan.client.swing.ECheckPanel;
import magellan.client.swing.InternationalizedDataPanel;
import magellan.client.swing.MagellanLookAndFeel;
import magellan.client.swing.MapperPanel;
import magellan.client.swing.MenuProvider;
import magellan.client.swing.MessagePanel;
import magellan.client.swing.ProgressBarUI;
import magellan.client.swing.StartWindow;
import magellan.client.swing.TipOfTheDay;
import magellan.client.swing.map.CellGeometry;
import magellan.client.swing.map.MapCellRenderer;
import magellan.client.swing.preferences.PreferencesAdapter;
import magellan.client.swing.preferences.PreferencesFactory;
import magellan.client.swing.tasks.TaskTablePanel;
import magellan.client.swing.tree.NodeWrapperFactory;
import magellan.client.utils.BookmarkDock;
import magellan.client.utils.BookmarkManager;
import magellan.client.utils.ErrorWindow;
import magellan.client.utils.FileHistory;
import magellan.client.utils.IconAdapterFactory;
import magellan.client.utils.LanguageDialog;
import magellan.client.utils.NameGenerator;
import magellan.client.utils.PluginSettingsFactory;
import magellan.client.utils.ProfileManager;
import magellan.client.utils.RendererLoader;
import magellan.client.utils.ResourceSettingsFactory;
import magellan.client.utils.SelectionHistory;
import magellan.library.CoordinateID;
import magellan.library.Faction;
import magellan.library.GameData;
import magellan.library.GameDataMerger;
import magellan.library.Message;
import magellan.library.MissingData;
import magellan.library.Region;
import magellan.library.TempUnit;
import magellan.library.Unit;
import magellan.library.event.GameDataEvent;
import magellan.library.event.GameDataListener;
import magellan.library.io.GameDataReader;
import magellan.library.io.cr.CRWriter;
import magellan.library.io.file.FileBackup;
import magellan.library.io.file.FileType;
import magellan.library.io.file.FileTypeFactory;
import magellan.library.io.file.FileType.ReadOnlyException;
import magellan.library.rules.Date;
import magellan.library.utils.JVMUtilities;
import magellan.library.utils.Locales;
import magellan.library.utils.Log;
import magellan.library.utils.MagellanFinder;
import magellan.library.utils.MagellanImages;
import magellan.library.utils.MemoryManagment;
import magellan.library.utils.NullUserInterface;
import magellan.library.utils.PropertiesHelper;
import magellan.library.utils.Regions;
import magellan.library.utils.Resources;
import magellan.library.utils.SelfCleaningProperties;
import magellan.library.utils.TrustLevels;
import magellan.library.utils.UserInterface;
import magellan.library.utils.Utils;
import magellan.library.utils.VersionInfo;
import magellan.library.utils.logging.Logger;
import magellan.library.utils.transformation.BoxTransformer;
import magellan.library.utils.transformation.MapTransformer.BBox;
import magellan.library.utils.transformation.MapTransformer.BBoxes;

/**
 * This class is the root of all evil. It represents also the main entry point into the application
 * and also the basic frame the application creates. It is a singleton which is instantiated from
 * {@link #main(String[])} and stored in {@link #INSTANCE}
 * 
 * @author $Author: $
 * @version $Revision: 388 $
 */
public class Client extends JFrame implements ShortcutListener, PreferencesFactory {
  private static final Logger log = Logger.getInstance(Client.class);

  public static final String SETTINGS_FILENAME = "magellan.ini";

  /** This is the instance of this class */
  public static Client INSTANCE = null;

  private List<JPanel> panels = null;

  private MapperPanel mapPanel = null;

  /** The overview panel */
  private EMapOverviewPanel overviewPanel = null;

  /** The details panel */
  private EMapDetailsPanel detailsPanel = null;

  /** The message panel */
  private MessagePanel messagePanel = null;

  /** The ECheck panel */
  private ECheckPanel echeckPanel = null;

  /** The open tasks panel */
  private TaskTablePanel taskPanel = null;

  private FileHistory fileHistory;

  private JMenu factionOrdersMenu;

  private JMenu factionOrdersMenuNot;

  private JMenu invertAllOrdersConfirmation;

  private List<NodeWrapperFactory> nodeWrapperFactories;

  private List<PreferencesFactory> preferencesAdapterList;

  private MenuAction saveAction;

  private OptionAction optionAction;

  private MagellanDesktop desktop;

  private ReportObserver reportState;

  /** Manager for setting and activating bookmarks. */
  private BookmarkManager bookmarkManager;

  /** Central undo manager - specialized to deliver change events */
  private MagellanUndoManager undoMgr = null;

  /** Directory for binaries */
  private static File binDirectory;

  /** Magellan Directories */
  private static File filesDirectory = null;

  /** Directory of "magellan.ini" etc. */
  private static File settingsDirectory = null;

  /** show order status in title */
  protected boolean showStatus = false;

  protected JMenuItem progressItem;

  /** start window, disposed after first init */
  protected static StartWindow startWindow;

  protected Collection<MagellanPlugIn> plugIns = new ArrayList<MagellanPlugIn>();

  /**
   * Creates a new Client object taking its data from <tt>gd</tt>.
   * <p>
   * Preferences are read from and stored in a file called <tt>magellan.ini</tt>. This file is
   * usually located in the user's home directory, which is the Windows directory in a Microsoft
   * Windows environment.
   * </p>
   * 
   * @param gd
   * @param binDir The directory where magellan files are situated
   * @param fileDir The directory where magellan configuration files are situated
   * @param settingsDir The directory where the settings are situated
   */
  protected Client(GameData gd, File binDir, File fileDir, File settingsDir) {
    Client.INSTANCE = this;
    Client.binDirectory = binDir;
    Client.filesDirectory = fileDir;
    Client.settingsDirectory = settingsDir;

    // get new dispatcher
    EventDispatcher dispatcher = new EventDispatcher();

    Client.startWindow.progress(1, Resources.get("clientstart.1"));
    Properties settings = Client.loadSettings(Client.settingsDirectory, Client.SETTINGS_FILENAME);
    String lastSavedVersion = null;
    if (settings == null) {
      Client.log.info("Client.loadSettings: settings file " + "magellan.ini"
          + " does not exist, using default values.");
      settings = new SelfCleaningProperties();
      settings.setProperty(PropertiesHelper.CLIENT_LOOK_AND_FEEL, "Windows");
      settings.setProperty(PropertiesHelper.ADVANCEDSHAPERENDERER_SETS, ",Einkaufsgut");
      settings.setProperty(PropertiesHelper.ADVANCEDSHAPERENDERER_CURRENT_SET, "Einkaufsgut");
      settings
          .setProperty(
              PropertiesHelper.ADVANCEDSHAPERENDERER + "Einkaufsgut"
                  + PropertiesHelper.ADVANCEDSHAPERENDERER_CURRENT,
              "\u00A7if\u00A7<\u00A7price\u00A7\u00D6l\u00A7-1\u00A71\u00A7else\u00A7if\u00A7<\u00A7price\u00A7Weihrauch\u00A7-1\u00A72\u00A7else\u00A7if\u00A7<\u00A7price\u00A7Seide\u00A7-1\u00A73\u00A7else\u00A7if\u00A7<\u00A7price\u00A7Myrrhe\u00A7-1\u00A74\u00A7else\u00A7if\u00A7<\u00A7price\u00A7Juwel\u00A7-1\u00A75\u00A7else\u00A7if\u00A7<\u00A7price\u00A7Gew\u00FCrz\u00A7-1\u00A76\u00A7else\u00A7if\u00A7<\u00A7price\u00A7Balsam\u00A7-1\u00A77\u00A7end\u00A7end\u00A7end\u00A7end\u00A7end\u00A7end\u00A7");
      settings.setProperty(PropertiesHelper.ADVANCEDSHAPERENDERER + "Einkaufsgut"
          + PropertiesHelper.ADVANCEDSHAPERENDERER_MAXIMUM, "10");
      settings
          .setProperty(
              PropertiesHelper.ADVANCEDSHAPERENDERER + "Einkaufsgut"
                  + PropertiesHelper.ADVANCEDSHAPERENDERER_COLORS,
              "0.0;223,131,39;0.12162162;220,142,24;0.14864865;153,153,153;0.23648648;153,153,153;0.26013514;204,255,255;0.3445946;204,255,255;0.3716216;0,204,0;0.42905405;0,204,0;0.46283785;255,51,0;0.5371622;255,51,0;0.5608108;255,255,0;0.6317568;255,255,0;0.6621622;51,51,255;1.0;0,51,255");
      settings.setProperty(PropertiesHelper.ADVANCEDSHAPERENDERER + "Einkaufsgut"
          + PropertiesHelper.ADVANCEDSHAPERENDERER_VALUES, "0.0;0.0;1.0;1.0");
      settings.setProperty(PropertiesHelper.ADVANCEDSHAPERENDERER + "Einkaufsgut"
          + PropertiesHelper.ADVANCEDSHAPERENDERER_MINIMUM, "0");
      // Message Panel Default colors.
      settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_EVENTS_COLOR, "#009999"); // Format:
      // #RRGGBB
      settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_MOVEMENTS_COLOR, "#000000");// Format:
      // #RRGGBB
      settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_ECONOMY_COLOR, "#000066");// Format:
      // #RRGGBB
      settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_MAGIC_COLOR, "#666600");// Format:
      // #RRGGBB
      settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_STUDY_COLOR, "#006666");// Format:
      // #RRGGBB
      settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_PRODUCTION_COLOR, "#009900");// Format:
      // #RRGGBB
      settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_ERRORS_COLOR, "#990000");// Format:
      // #RRGGBB
      settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_BATTLE_COLOR, "#999900");// Format:
      // #RRGGBB

      // try to set path to ECheck
      initECheckPath(settings);

      initLocales(settings, true);
    } else {
      initLocales(settings, false);

      // backward compatibility for white message tags (it's now the text color)
      if (settings.getProperty(PropertiesHelper.MESSAGETYPE_SECTION_EVENTS_COLOR, "-").equals(
          "#FFFFFF")) {
        settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_EVENTS_COLOR, "#009999"); // Format:
        // #RRGGBB
      }
      if (settings.getProperty(PropertiesHelper.MESSAGETYPE_SECTION_MOVEMENTS_COLOR, "-").equals(
          "#FFFFFF")) {
        settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_MOVEMENTS_COLOR, "#000000");// Format:
        // #RRGGBB
      }
      if (settings.getProperty(PropertiesHelper.MESSAGETYPE_SECTION_ECONOMY_COLOR, "-").equals(
          "#FFFFFF")) {
        settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_ECONOMY_COLOR, "#000066");// Format:
        // #RRGGBB
      }
      if (settings.getProperty(PropertiesHelper.MESSAGETYPE_SECTION_MAGIC_COLOR, "-").equals(
          "#FFFFFF")) {
        settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_MAGIC_COLOR, "#666600");// Format:
        // #RRGGBB
      }
      if (settings.getProperty(PropertiesHelper.MESSAGETYPE_SECTION_STUDY_COLOR, "-").equals(
          "#FFFFFF")) {
        settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_STUDY_COLOR, "#006666");// Format:
        // #RRGGBB
      }
      if (settings.getProperty(PropertiesHelper.MESSAGETYPE_SECTION_PRODUCTION_COLOR, "-").equals(
          "#FFFFFF")) {
        settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_PRODUCTION_COLOR, "#009900");// Format:
        // #RRGGBB
      }
      if (settings.getProperty(PropertiesHelper.MESSAGETYPE_SECTION_ERRORS_COLOR, "-").equals(
          "#FFFFFF")) {
        settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_ERRORS_COLOR, "#990000");// Format:
        // #RRGGBB
      }
      if (settings.getProperty(PropertiesHelper.MESSAGETYPE_SECTION_BATTLE_COLOR, "-").equals(
          "#FFFFFF")) {
        settings.setProperty(PropertiesHelper.MESSAGETYPE_SECTION_BATTLE_COLOR, "#999900");// Format:
        // #RRGGBB
      }

      lastSavedVersion = settings.getProperty("Client.Version");
      if (lastSavedVersion == null) {
        lastSavedVersion = "null";
      }
    }
    if (VersionInfo.getVersion(fileDir) != null) {
      settings.setProperty("Client.Version", VersionInfo.getVersion(fileDir));
    }
    if (lastSavedVersion != null) {
      settings.setProperty("Client.LastVersion", lastSavedVersion);
    }

    showStatus = PropertiesHelper.getBoolean(settings, "Client.ShowOrderStatus", false);

    Properties completionSettings =
        Client.loadSettings(Client.settingsDirectory, "magellan_completions.ini");
    if (completionSettings == null) {
      completionSettings = new SelfCleaningProperties();
    }

    // initialize the context, this has to be very early.
    context = new MagellanContext(this);
    context.setEventDispatcher(dispatcher);
    context.setProperties(settings);
    context.setCompletionProperties(completionSettings);
    context.init();

    context.setGameData(gd);
    // init icon, fonts, repaint shortcut, L&F, window things
    initUI();

    // create management and observer objects
    dispatcher.addSelectionListener(SelectionHistory.getSelectionEventHook());
    dispatcher.addTempUnitListener(SelectionHistory.getTempUnitEventHook());
    bookmarkManager = new BookmarkManager(dispatcher);
    undoMgr = new MagellanUndoManager();
    reportState = new ReportObserver(dispatcher);

    // load plugins
    initPlugIns();

    // init components
    Client.startWindow.progress(2, Resources.get("clientstart.2"));
    panels = new LinkedList<JPanel>();
    nodeWrapperFactories = new LinkedList<NodeWrapperFactory>();

    List<Container> topLevelComponents = new LinkedList<Container>();
    Map<String, Component> components = initComponents(topLevelComponents);

    // dispatcher.addGameDataListener(Units.getGameDataListener());

    // init desktop
    Client.startWindow.progress(3, Resources.get("clientstart.3"));
    Rectangle bounds = PropertiesHelper.loadRect(settings, null, "Client");
    if (bounds != null) {
      setBounds(bounds);
    }

    desktop = MagellanDesktop.getInstance();
    desktop.init(this, context, settings, components, Client.settingsDirectory);

    setContentPane(desktop);

    // load plugins
    // initPlugIns();

    // do it here because we need the desktop menu
    setJMenuBar(createMenuBar(topLevelComponents));

    // enable EventDisplayer
    // new
    // com.eressea.util.logging.EventDisplayDialog(this,false,dispatcher).setVisible(true);
  }

  // ////////////////////////
  // BASIC initialization //
  // ////////////////////////

  private MagellanContext context;

  /**
   * Load the file fileName in the given directory into the settings object.
   */
  public static Properties loadSettings(File directory, String fileName) {
    Properties settings = new SelfCleaningProperties();
    // settings = new OrderedOutputProperties();
    // settings = new AgingProperties();

    settings.clear();

    File settingsFile = new File(directory, fileName);

    // load settings from file
    if (settingsFile.exists()) {
      try {
        settings.load(new BufferedInputStream(new FileInputStream(settingsFile)));
        Client.log.info("Client.loadSettings: successfully loaded " + settingsFile);
      } catch (IOException e) {
        Client.log.error("Client.loadSettings: Error while loading " + settingsFile, e);
        return null;
      }
    } else
      return null;
    return settings;
  }

  protected void initLocales(Properties settings, boolean ask) {
    if (ask) {
      LanguageDialog ld = new LanguageDialog(Client.startWindow, settings);

      if (ld.languagesFound()) {
        // startWindow.toBack();
        Point p = Client.startWindow.getLocation();
        ld.setLocation((int) p.getX() + (Client.startWindow.getWidth() - ld.getWidth()) / 2,
            (int) p.getY() - ld.getHeight() / 2);
        Locale locale = ld.show();
        // startWindow.toFront();
        if (locale == null) {
          // without this decision we cannot start the application
          Client.log.error("can't work without locale");
          quit(false);
        } else if (!locale.equals(Locale.getDefault())) {
          settings.setProperty("locales.gui", locale.getLanguage());
          settings.setProperty("locales.orders", locale.getLanguage());
        }
      }
    }

    if (settings.getProperty("locales.gui") != null) {
      Locales.setGUILocale(new Locale(settings.getProperty("locales.gui")));
    } else {
      Locales.setGUILocale(Locale.getDefault());
    }
    if (settings.getProperty("locales.orders") != null) {
      Locales.setOrderLocale(new Locale(settings.getProperty("locales.orders")));
    } else {
      Locales.setOrderLocale(Locale.GERMAN);
    }
    Client.log.info("GUI locale: " + Locales.getGUILocale() + settings.getProperty("locales.gui")
        + ", orders locale: " + Locales.getOrderLocale() + settings.getProperty("locales.orders"));
  }

  // TODO (stm) this is used by exactly once in the whole project. Why do we
  // need context anyway?
  /**
   * Returns the MagellanContext
   */
  public MagellanContext getMagellanContext() {
    return context;
  }

  /**
   * Returns the message panel.
   */
  public MessagePanel getMessagePanel() {
    return messagePanel;
  }

  /**
   * Returns the application icon
   * 
   * @return the application icon
   */
  public static Image getApplicationIcon() {
    // set the application icon
    ImageIcon icon = MagellanImages.ABOUNT_APPLICATION_ICON;

    return (icon == null) ? null : icon.getImage();
  }

  /**
   * Inits base UI things: # frame icon # window event things # fonts # repaint shortcut # L&F
   */
  protected void initUI() {
    Image iconImage = Client.getApplicationIcon();

    // set the application icon
    if (iconImage != null) {
      setIconImage(iconImage);
    }

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        quit(true);
      }
    });

    /* setup font size */
    try {
      float fScale = PropertiesHelper.getFloat(getProperties(), "Client.FontScale", 1.0f);

      if (fScale != 1.0f) {
        // TODO(pavkovic): the following code bloats the fonts in an
        // undesired way, perhaps
        // we remove this configuration option?
        UIDefaults table = UIManager.getDefaults();
        Enumeration<?> eKeys = table.keys();

        while (eKeys.hasMoreElements()) {
          Object obj = eKeys.nextElement();
          Font font = UIManager.getFont(obj);

          if (font != null) {
            font = new FontUIResource(font.deriveFont(font.getSize2D() * fScale));
            UIManager.put(obj, font);
          }
        }
      }
    } catch (Exception e) {
      Client.log.error(e);
    }

    // initialize client shortcut - F5 to repaint
    DesktopEnvironment.registerShortcutListener(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), this);

    // init L&F
    initLookAndFeels();
  }

  // ////////////////////////////
  // COMPONENT initialization //
  // ////////////////////////////

  /**
   * Initializes the Magellan components. The returned hashtable holds all components with
   * well-known desktop keywords.
   * 
   * @param topLevel
   */
  protected Map<String, Component> initComponents(List<Container> topLevel) {
    Map<String, Component> components = new Hashtable<String, Component>();

    // configure and add map panel
    // get cell geometry
    CellGeometry geo = new CellGeometry("cellgeometry.txt");

    // load custom renderers
    // ForcedFileClassLoader.directory = filesDirectory;
    RendererLoader rl = new RendererLoader(Client.filesDirectory, ".", geo, getProperties());
    Collection<MapCellRenderer> cR = rl.loadRenderers();

    // init mapper
    mapPanel = new MapperPanel(getMagellanContext(), cR, geo);
    mapPanel.setMinimumSize(new Dimension(100, 10));
    panels.add(mapPanel);
    components.put("MAP", mapPanel);
    components.put("MINIMAP", mapPanel.getMinimapComponent());
    topLevel.add(mapPanel);

    // configure and add message panel
    messagePanel = new MessagePanel(getDispatcher(), getData(), getProperties());
    messagePanel.setMinimumSize(new Dimension(100, 10));
    panels.add(messagePanel);
    nodeWrapperFactories.add(messagePanel.getNodeWrapperFactory());
    components.put("MESSAGES", messagePanel);
    topLevel.add(messagePanel);

    // configure and add details panel
    detailsPanel = new EMapDetailsPanel(getDispatcher(), getData(), getProperties(), undoMgr);
    detailsPanel.setMinimumSize(new Dimension(100, 10));
    panels.add(detailsPanel);
    nodeWrapperFactories.add(detailsPanel.getNodeWrapperFactory());

    Container c = detailsPanel.getNameAndDescriptionPanel();
    components.put("NAME&DESCRIPTION", c);
    components.put("NAME", c.getComponent(0));
    components.put("DESCRIPTION", c.getComponent(1));
    components.put("DETAILS", detailsPanel.getDetailsPanel());
    components.put("ORDERS", detailsPanel.getOrderEditor());

    // this keyword is deprecated
    components.put("COMMANDS", detailsPanel.getOrderEditor());
    topLevel.add(detailsPanel);

    // configure and add overview panel
    overviewPanel = new EMapOverviewPanel(getDispatcher(), getProperties());
    overviewPanel.setMinimumSize(new Dimension(100, 10));
    panels.add(overviewPanel);
    components.put(EMapOverviewPanel.IDENTIFIER, overviewPanel.getOverviewComponent());
    components.put("HISTORY", overviewPanel.getHistoryComponent());
    components.put("OVERVIEW&HISTORY", overviewPanel);
    nodeWrapperFactories.add(overviewPanel.getNodeWrapperFactory());
    topLevel.add(overviewPanel);

    echeckPanel =
        new ECheckPanel(getDispatcher(), getData(), getProperties(), getSelectedRegions().values());
    components.put(ECheckPanel.IDENTIFIER, echeckPanel);

    taskPanel = new TaskTablePanel(getDispatcher(), getData(), getProperties());
    components.put(TaskTablePanel.IDENTIFIER, taskPanel);

    components.put(DebugDock.IDENTIFIER, DebugDock.getInstance());
    components.put(BookmarkDock.IDENTIFIER, BookmarkDock.getInstance());

    // armyStatsPanel = new ArmyStatsPanel(getDispatcher(), getData(), getProperties(), true);
    // components.put(ArmyStatsPanel.IDENTIFIER, armyStatsPanel);

    // tradeOrganizer = new TradeOrganizer(this, getDispatcher(), getData(), getProperties());
    // components.put(TradeOrganizer.IDENTIFIER, tradeOrganizer);

    Client.log.info("Checking for dock-providers...(MagellanPlugIns)");
    for (MagellanPlugIn plugIn : plugIns) {
      Map<String, Component> plugInDocks = plugIn.getDocks();
      if (plugInDocks != null && plugInDocks.size() > 0) {
        components.putAll(plugInDocks);
      }
    }

    return components;
  }

  // //////////////////////////
  // MENUBAR initialization //
  // //////////////////////////

  /**
   * Creates a menu bar to be added to this frame.
   * 
   * @param components
   */
  private JMenuBar createMenuBar(Collection<Container> components) {
    JMenuBar menuBar = new JMenuBar();

    // create static menus
    menuBar.add(createFileMenu());
    menuBar.add(createEditMenu());
    menuBar.add(createOrdersMenu());
    menuBar.add(createBookmarkMenu());
    menuBar.add(createMapMenu());

    // create dynamix menus -- currently not used (stm)
    Map<String, JMenu> topLevel = new HashMap<String, JMenu>();
    List<JMenu> direction = new LinkedList<JMenu>();
    Iterator<Container> it = components.iterator();
    Client.log.info("Checking for menu-providers...");

    while (it.hasNext()) {
      Container o = it.next();

      if (o instanceof MenuProvider) {
        MenuProvider mp = (MenuProvider) o;

        if (mp.getSuperMenu() != null) {
          if (!topLevel.containsKey(mp.getSuperMenu())) {
            topLevel.put(mp.getSuperMenu(), new JMenu(mp.getSuperMenuTitle()));
            direction.add(topLevel.get(mp.getSuperMenu()));
          }

          JMenu top = topLevel.get(mp.getSuperMenu());
          top.add(mp.getMenu());
        } else {
          direction.add(mp.getMenu());
        }
      }
    }

    // currently not used (stm)
    // for (JMenu menu:topLevel.values()){
    // menuBar.add(menu);
    // }

    // desktop and extras last
    menuBar.add(desktop.getDesktopMenu());

    // add external modules if some can be found
    JMenu plugInMenu = null;
    Client.log.info("Checking for menu-providers...(MagellanPlugIns)");
    for (MagellanPlugIn plugIn : plugIns) {
      List<JMenuItem> plugInMenuItems = plugIn.getMenuItems();
      if (plugInMenuItems != null && plugInMenuItems.size() > 0) {
        if (plugInMenu == null) {
          plugInMenu = new JMenu(Resources.get("client.menu.plugins.caption"));
          plugInMenu.setMnemonic(Resources.get("client.menu.plugins.mnemonic").charAt(0));
          menuBar.add(plugInMenu);
        } else {
          plugInMenu.addSeparator();
        }
        for (JMenuItem menuItem : plugInMenuItems) {
          plugInMenu.add(menuItem);
        }
      }
    }

    // the special menu - a plugin with own main menu entry
    for (MagellanPlugIn plugIn : plugIns) {
      if (plugIn instanceof MainMenuProvider) {
        MainMenuProvider p = (MainMenuProvider) plugIn;
        JMenu newJMenu = p.getJMenu();
        if (newJMenu != null) {
          menuBar.add(newJMenu);
        }
      }
    }

    menuBar.add(createExtrasMenu());
    return menuBar;
  }

  protected JMenu createFileMenu() {
    JMenu file = new JMenu(Resources.get("client.menu.file.caption"));
    file.setMnemonic(Resources.get("client.menu.file.mnemonic").charAt(0));
    addMenuItem(file, new OpenCRAction(this));
    addMenuItem(file, new AddCRAction(this));
    addMenuItem(file, new OpenOrdersAction(this));
    file.addSeparator();
    saveAction = new FileSaveAction(this);
    addMenuItem(file, saveAction);
    addMenuItem(file, new FileSaveAsAction(this));
    file.addSeparator();
    addMenuItem(file, new SaveOrdersAction(this, SaveOrdersAction.Mode.DIALOG));
    addMenuItem(file, new SaveOrdersAction(this, SaveOrdersAction.Mode.MAIL));
    addMenuItem(file, new SaveOrdersAction(this, SaveOrdersAction.Mode.FILE));
    addMenuItem(file, new SaveOrdersAction(this, SaveOrdersAction.Mode.CLIPBOARD));
    file.addSeparator();
    addMenuItem(file, new ExportCRAction(this));
    file.addSeparator();

    // now create the file history since we have all data
    fileHistory = new FileHistory(this, getProperties(), file, file.getItemCount());
    fileHistory.buildFileHistoryMenu();
    file.addSeparator();
    addMenuItem(file, new AbortAction(this));
    addMenuItem(file, new QuitAction(this));

    return file;
  }

  protected JMenu createEditMenu() {
    JMenu edit = new JMenu(Resources.get("client.menu.edit.caption"));
    edit.setMnemonic(Resources.get("client.menu.edit.mnemonic").charAt(0));
    addMenuItem(edit, new UndoAction(this, undoMgr));
    addMenuItem(edit, new RedoAction(this, undoMgr));
    edit.addSeparator();
    addMenuItem(edit, new FindAction(this));
    addMenuItem(edit, new QuickFindAction(this));

    return edit;
  }

  protected JMenu createOrdersMenu() {
    JMenu ordersMenu = new JMenu(Resources.get("client.menu.orders.caption"));
    ordersMenu.setMnemonic(Resources.get("client.menu.orders.mnemonic").charAt(0));
    addMenuItem(ordersMenu, new UnconfirmAction(this, overviewPanel));
    addMenuItem(ordersMenu, new FindPreviousUnconfirmedAction(this, overviewPanel));

    addMenuItem(ordersMenu, new ConfirmAction(this, overviewPanel));

    // add factionordersmenu to ordersmenu
    factionOrdersMenu = new JMenu(Resources.get("client.menu.orders.all.caption"));
    factionOrdersMenu.setMnemonic(Resources.get("client.menu.orders.all.mnemonic").charAt(0));
    ordersMenu.add(factionOrdersMenu);

    // add factionordersmenunot to ordersmenu
    factionOrdersMenuNot = new JMenu(Resources.get("client.menu.orders.allnot.caption"));
    factionOrdersMenuNot.setMnemonic(Resources.get("client.menu.orders.allnot.mnemonic").charAt(0));
    ordersMenu.add(factionOrdersMenuNot);

    // add factionordersmenu to ordersmenu
    invertAllOrdersConfirmation = new JMenu(Resources.get("client.menu.orders.invert.caption"));
    invertAllOrdersConfirmation.setMnemonic(Resources.get("client.menu.orders.invert.mnemonic")
        .charAt(0));
    ordersMenu.add(invertAllOrdersConfirmation);

    updateConfirmMenu();

    return ordersMenu;
  }

  private void refillChangeFactionConfirmation(JMenu aMenu, int aConfirmationType) {
    if (aMenu.getItemCount() == 0) {
      // fill basic faction "all units"
      addMenuItem(aMenu, new ChangeFactionConfirmationAction(this, null, aConfirmationType, false,
          false));
      addMenuItem(aMenu, new ChangeFactionConfirmationAction(this, null, aConfirmationType, true,
          false));
      addMenuItem(aMenu, new ChangeFactionConfirmationAction(this, null, aConfirmationType, false,
          true));
      addMenuItem(aMenu, new ChangeFactionConfirmationAction(this, null, aConfirmationType, true,
          true));
    } else {
      JMenuItem all = aMenu.getItem(0);
      JMenuItem allSel = aMenu.getItem(1);
      JMenuItem spy = aMenu.getItem(2);
      JMenuItem spySel = aMenu.getItem(3);
      aMenu.removeAll();
      aMenu.add(all);
      aMenu.add(allSel);
      aMenu.add(spy);
      aMenu.add(spySel);
    }

    if (getData() != null) {
      // add all privileged factions
      for (Faction f : getData().getFactions()) {
        if ((f.isPrivileged()) && !f.units().isEmpty()) {
          aMenu.add(new ChangeFactionConfirmationAction(this, f, aConfirmationType, false, false));
          aMenu.add(new ChangeFactionConfirmationAction(this, f, aConfirmationType, true, false));
        }
      }
    }
  }

  protected JMenu createMapMenu() {
    JMenu map = new JMenu(Resources.get("client.menu.map.caption"));
    map.setMnemonic(Resources.get("client.menu.map.mnemonic").charAt(0));
    addMenuItem(map, new SetOriginAction(this));
    addMenuItem(map, new SetGirthAction(this));
    addMenuItem(map, new IslandAction(this));
    addMenuItem(map, new MapSaveAction(this, mapPanel));
    map.addSeparator();
    addMenuItem(map, new SelectAllAction(this));
    addMenuItem(map, new SelectNothingAction(this));
    addMenuItem(map, new InvertSelectionAction(this));
    addMenuItem(map, new SelectIslandsAction(this));
    addMenuItem(map, new FillSelectionAction(this));
    addMenuItem(map, new ExpandSelectionAction(this));
    map.addSeparator();
    addMenuItem(map, new OpenSelectionAction(this));
    addMenuItem(map, new AddSelectionAction(this));
    addMenuItem(map, new SaveSelectionAction(this));

    return map;
  }

  protected JMenu createBookmarkMenu() {
    JMenu bookmarks = new JMenu(Resources.get("client.menu.bookmarks.caption"));
    bookmarks.setMnemonic(Resources.get("client.menu.bookmarks.mnemonic").charAt(0));

    JMenuItem toggle = new JMenuItem(Resources.get("client.menu.bookmarks.toggle.caption"));
    toggle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_MASK));
    toggle.addActionListener(new ToggleBookmarkAction());
    bookmarks.add(toggle);

    JMenuItem forward = new JMenuItem(Resources.get("client.menu.bookmarks.forward.caption"));
    forward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
    forward.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bookmarkManager.jumpForward();
      }
    });
    bookmarks.add(forward);

    JMenuItem backward = new JMenuItem(Resources.get("client.menu.bookmarks.backward.caption"));
    backward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.SHIFT_MASK));
    backward.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bookmarkManager.jumpBackward();
      }
    });
    bookmarks.add(backward);

    JMenuItem clear = new JMenuItem(Resources.get("client.menu.bookmarks.clear.caption"));
    clear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bookmarkManager.clearBookmarks();
      }
    });
    bookmarks.add(clear);

    return bookmarks;
  }

  protected JMenu createExtrasMenu() {
    JMenu extras = new JMenu(Resources.get("client.menu.extras.caption"));
    extras.setMnemonic(Resources.get("client.menu.extras.mnemonic").charAt(0));
    addMenuItem(extras, new FactionStatsAction(this));
    addMenuItem(extras, new ArmyStatsAction(this));
    addMenuItem(extras, new TradeOrganizerAction(this));
    addMenuItem(extras, new AlchemyAction(this));

    // addMenuItem(extras, new TaskTableAction(this));
    // addMenuItem(extras, new ECheckAction(this));
    addMenuItem(extras, new VorlageAction(this));
    extras.addSeparator();
    addMenuItem(extras, new ConversionAction(this));
    extras.addSeparator();
    addMenuItem(extras, new RepaintAction(this));
    addMenuItem(extras, new TileSetAction(this, mapPanel));
    extras.addSeparator();
    preferencesAdapterList = new ArrayList<PreferencesFactory>(8);
    preferencesAdapterList.add(this);
    preferencesAdapterList.add(desktop);
    preferencesAdapterList.add(overviewPanel);
    preferencesAdapterList.add(detailsPanel);
    preferencesAdapterList.add(mapPanel);
    preferencesAdapterList.add(taskPanel);
    preferencesAdapterList.add(new IconAdapterFactory(nodeWrapperFactories));
    preferencesAdapterList.add(new ResourceSettingsFactory(plugIns, getProperties()));

    preferencesAdapterList.add(new PluginSettingsFactory(plugIns, getProperties()));

    optionAction = new OptionAction(this, preferencesAdapterList);
    addMenuItem(extras, optionAction);
    addMenuItem(extras, new ProfileAction(this));

    // TODO(pavkovic): currently EresseaOptionPanel is broken, I deactivated
    // it.
    extras.addSeparator();
    addMenuItem(extras, new HelpAction(this));
    addMenuItem(extras, new TipOfTheDayAction(this));
    extras.addSeparator();
    addMenuItem(extras, new InfoAction(this));

    return extras;
  }

  /**
   * @param font
   * @return
   * @deprecated As of Java 1.2, the Font method getLineMetrics should be used.
   */
  @Deprecated
  public static FontMetrics getDefaultFontMetrics(Font font) {
    return Toolkit.getDefaultToolkit().getFontMetrics(font);
  }

  private class ToggleBookmarkAction implements ActionListener, SelectionListener {
    private Object activeObject;

    /**
     * Creates a new ToggleBookmarkAction object.
     */
    public ToggleBookmarkAction() {
      getDispatcher().addSelectionListener(this);
    }

    /**
     * Changes the active object
     * 
     * @see magellan.client.event.SelectionListener#selectionChanged(magellan.client.event.SelectionEvent)
     */
    public void selectionChanged(SelectionEvent se) {
      if (se.isSingleSelection()) {
        activeObject = se.getActiveObject();
      } else {
        activeObject = null;
      }
      if (se.getActiveObject() instanceof Region) {
        getData().setActiveRegion((Region) se.getActiveObject());
      }
    }

    /**
     * Bookmarks the active object.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      if (activeObject != null) {
        bookmarkManager.toggleBookmark(activeObject);
      }
    }
  }

  /**
   * Adds a new menu item to the specifie menu associating it with the specified action, setting its
   * mnemonic and registers its accelerator if it has one.
   * 
   * @return the menu item created.
   */
  private JMenuItem addMenuItem(JMenu parentMenu, MenuAction action) {
    JMenuItem item = parentMenu.add(action);
    item.setMnemonic(action.getMnemonic());

    if (action.getAccelerator() != null) {
      DesktopEnvironment.registerActionListener(action.getAccelerator(), action);
      item.setAccelerator(action.getAccelerator());
    }

    new MenuActionObserver(item, action);

    return item;
  }

  /**
   * START & END Code
   */
  public static void main(String args[]) {
    try {
      /* set the stderr to stdout while there is no log attached */
      System.setErr(System.out);

      // Fiete 20061208
      // set finalizer prio to max
      magellan.library.utils.MemoryManagment.setFinalizerPriority(Thread.MAX_PRIORITY);

      Parameters parameters = parseCommandLine(args);
      if (parameters == null)
        return;

      /* determine default value for files directory */
      parameters.binDir = MagellanFinder.findMagellanDirectory();

      if (parameters.resourceDir == null) {
        parameters.resourceDir = parameters.binDir;
      }
      parameters.settingsDir =
          MagellanFinder.findSettingsDirectory(parameters.resourceDir, parameters.settingsDir);
      Resources.getInstance().initialize(parameters.resourceDir, "");
      MagellanLookAndFeel.setMagellanDirectory(parameters.resourceDir);
      MagellanImages.setMagellanDirectory(parameters.resourceDir);

      // initialize start window
      Icon startIcon = MagellanImages.ABOUT_MAGELLAN;

      Client.startWindow = new StartWindow(startIcon, 5, parameters.resourceDir);
      Client.startWindow.setVisible(true);
      Client.startWindow.progress(0, Resources.get("clientstart.0"));

      ProfileManager.init(parameters);
      if (ProfileManager.getProfileDirectory() == null || ProfileManager.isAlwaysAsk()) {
        if (!ProfileManager.showProfileChooser(Client.startWindow)) {
          log.info("Abort requested by ProfileChooser");
          System.exit(0);
        } else {
          ProfileManager.saveSettings();
        }
      }
      parameters.settingsDir = ProfileManager.getProfileDirectory();

      // tell the user where we expect ini files and errors.txt
      PropertiesHelper.setSettingsDirectory(parameters.settingsDir);

      startLog(parameters);

      final File tBinDir = parameters.binDir;
      final File tFileDir = parameters.resourceDir;
      final File tsettFileDir = ProfileManager.getProfileDirectory();
      final String tReport = parameters.report;

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          // can't call loadRules from here, so we initially work with an
          // empty ruleset.
          // This is not very nice, though...
          GameData data = new MissingData();

          // new CompleteData(new com.eressea.rules.Eressea(), "void");
          Client c = new Client(data, tBinDir, tFileDir, tsettFileDir);
          // setup a singleton instance of this client
          Client.INSTANCE = c;

          String newestVersion =
              VersionInfo.getNewestVersion(c.getProperties(), Client.startWindow);
          String currentVersion = VersionInfo.getVersion(tFileDir);
          if (!Utils.isEmpty(newestVersion)) {
            Client.log.info("Newest Version on server: " + newestVersion);
            Client.log.info("Current Version: " + currentVersion);
            if (VersionInfo.isNewer(newestVersion, currentVersion)) {
              JOptionPane.showMessageDialog(Client.startWindow, Resources.get("client.new_version",
                  new Object[] { newestVersion }));
            }
          }

          String lastVersion = c.getProperties().getProperty("Client.LastVersion");
          if (lastVersion == null || !lastVersion.equals(currentVersion)) {
            UpdateDialog dlg = new UpdateDialog(c, lastVersion, currentVersion);
            dlg.setVisible(true);
            if (!dlg.getResult()) {
              c.quit(false);
            }
          }

          File crFile = null;

          if (tReport == null) {
            // if no report is given on startup, we check if we can load the last
            // loaded report.
            boolean loadLastReport =
                PropertiesHelper.getBoolean(c.getProperties(),
                    PropertiesHelper.CLIENTPREFERENCES_LOAD_LAST_REPORT, true);
            if (loadLastReport) {
              crFile = c.fileHistory.getLastExistingReport();
              if (crFile == null) {
                // okay, ask for a file...
                crFile = OpenCRAction.getFileFromFileChooser(c, Client.startWindow);
              }
            }
          } else {
            crFile = new File(tReport);
          }

          if (crFile != null) {
            Client.startWindow.progress(4, Resources.get("clientstart.4"));

            c.loadCRThread(crFile);
          }

          c.setReportChanged(false);

          Client.startWindow.progress(5, Resources.get("clientstart.5"));
          c.setAllVisible(true);
          Client.startWindow.setVisible(false);
          Client.startWindow.dispose();
          Client.startWindow = null;

          // show tip of the day window
          if (c.getProperties().getProperty("TipOfTheDay.showTips", "true").equals("true")
              || c.getProperties().getProperty("TipOfTheDay.firstTime", "true").equals("true")) {
            TipOfTheDay totd = new TipOfTheDay(c, c.getProperties());

            if (totd.doShow()) {
              // totd.setVisible(true);
              totd.showTipDialog();
              totd.showNextTip();
            }
          }
        }
      });
    } catch (Throwable exc) { // any fatal error
      Client.log.error(exc); // print it so it can be written to errors.txt

      // try to create a nice output
      String out = "A fatal error occured: " + exc.toString();

      Client.log.error(out, exc);
      JOptionPane.showMessageDialog(new JFrame(), out);
      System.exit(1);
    }
  }

  /**
   * Open the log file and log basic information to it.
   * 
   * @param parameters
   * @throws IOException if an I/O error occurs
   */
  protected static void startLog(Parameters parameters) throws IOException {
    // now redirect stderr through our log
    Log LOG = new Log(parameters.settingsDir);
    System.setErr(LOG.getPrintStream());

    // logging with level warning to get this information even if user selected low debug level...
    Logger.activateDefaultLogListener(true);
    Client.log.warn("Start writing error file with encoding " + LOG.encoding + ", log level "
        + Logger.getLevel(Logger.getLevel()));

    Client.log.info("resource directory: " + parameters.resourceDir);
    Client.log.info("settings directory: " + parameters.settingsDir);

    String version = VersionInfo.getVersion(parameters.binDir);
    if (version == null) {
      version = VersionInfo.getVersion(parameters.resourceDir);
    }
    if (version == null) {
      Client.log.warn("no magellan version available");
    } else {
      Client.log.warn("This is Magellan Version " + version);
    }

    try {
      Client.log.warn("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch")
          + " " + System.getProperty("os.version"));
      Client.log.warn("Java Version: " + System.getProperty("java.version") + " "
          + System.getProperty("java.vendor"));
      Client.log.warn("Java Spec: " + System.getProperty("java.specification.version") + " "
          + System.getProperty("java.specification.vendor") + " "
          + System.getProperty("java.specification.name"));
      Client.log.warn("VM Version: " + System.getProperty("java.vm.version") + " "
          + System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name"));
      Client.log.warn("VM Specification: " + System.getProperty("java.vm.specification.version")
          + " " + System.getProperty("java.vm.specification.vendor") + " "
          + System.getProperty("java.vm.specification.name"));
      Client.log.warn("Java Class Version: " + System.getProperty("java.class.version"));
    } catch (SecurityException e) {
      Client.log.warn("Unable to retrieve system properties: " + e);
    }
  }

  /**
   * Stores command line parameters
   */
  public static class Parameters {
    /** the program directory */
    public File binDir;
    /** the directory for resources (images, languages etc.) */
    public File resourceDir;
    /** the directory to store ini files and stuff in */
    public File settingsDir;
    /** the name of the profile */
    public String profile;
    /** the report to be loaded on startup */
    public String report;
  }

  protected static Parameters parseCommandLine(String[] args) {
    Parameters result = new Parameters();

    /* process command line parameters */
    for (int i = 0; i < args.length; ++i) {
      if (args[i].toLowerCase().startsWith("-log")) {
        String level = null;

        if (args[i].toLowerCase().startsWith("-log=") && (args[i].length() > 5)) {
          level = args[i].charAt(5) + "";
        } else if (args[i].equals("-log") && (args.length > (i + 1))) {
          i++;
          level = args[i];
        }

        if (level != null) {
          level = level.toUpperCase();
          Logger.setLevel(level);
          Client.log.info("Client.main: Set logging to " + level);

          if ("A".equals(level)) {
            Client.log.awt("Start logging of awt events to awtdebug.txt.");
          }
        }
      } else if (args[i].equals("--help")) {
        Help.open(args);
        return null;
      } else if (args[i].equals("-d") && (args.length > (i + 1))) {
        i++;

        try {
          File tmpFile = new File(args[i]).getCanonicalFile();

          if (tmpFile.exists() && tmpFile.isDirectory() && tmpFile.canWrite()) {
            result.resourceDir = tmpFile;
          } else {
            Client.log.info("Client.main(): the specified files directory does not "
                + "exist, is not a directory or is not writeable.");
          }
        } catch (Exception e) {
          Client.log.error("Client.main(): the specified files directory is invalid.", e);
        }
      } else if (args[i].equals("-s") && (args.length > (i + 1))) {
        i++;

        try {
          File tmpFile = new File(args[i]).getCanonicalFile();

          if (tmpFile.exists() && tmpFile.isDirectory() && tmpFile.canWrite()) {
            result.settingsDir = tmpFile;
          } else {
            Client.log.info("Client.main(): the specified files directory does not "
                + "exist, is not a directory or is not writeable.");
          }
        } catch (Exception e) {
          Client.log.error("Client.main(): the specified files directory is invalid.", e);
        }
      } else if (args[i].equals("-p") && (args.length > (i + 1))) {
        i++;

        result.profile = args[i];
      } else {
        if (args[i].toLowerCase().endsWith(".cr") || args[i].toLowerCase().endsWith(".bz2")
            || args[i].toLowerCase().endsWith(".zip")) {
          result.report = args[i];
        }
      }

    }
    return result;
  }

  /**
   * Asks the user whether the current report should be saved but does not do it!
   * 
   * @return {@link JOptionPane#YES_OPTION} if the user selected to save the report,
   *         {@link JOptionPane#NO_OPTION} if the user selected not to save it,
   *         {@link JOptionPane#CANCEL_OPTION} if saving is not possible or the user selected to
   *         cancel the operation.
   */
  public int askToSave() {
    if (reportState.isStateChanged()) {
      String msg = null;

      try {
        if (getData() != null && getData().getFileType() != null
            && getData().getFileType().getFile() != null) {
          Object msgArgs[] = { getData().getFileType().getFile().getAbsolutePath() };
          msg =
              (new MessageFormat(Resources.get("client.msg.quit.confirmsavefile.text")))
                  .format(msgArgs);
        } else {
          msg = Resources.get("client.msg.quit.confirmsavenofile.text");
        }
      } catch (IOException io) {
        Client.log.error("", io);
        msg = Resources.get("client.msg.quit.confirmsavenofile.text");
      }

      switch (JOptionPane.showConfirmDialog(this, msg, Resources
          .get("client.msg.quit.confirmsave.title"), JOptionPane.YES_NO_CANCEL_OPTION)) {
      case JOptionPane.YES_OPTION:

        return JOptionPane.YES_OPTION;

      case JOptionPane.CANCEL_OPTION:
        return JOptionPane.CANCEL_OPTION;
      }
    }

    return JOptionPane.NO_OPTION;
  }

  /**
   * Saves the current data and waits until saving is finished.
   * 
   * @return <code>true</code> if data was successfully saved.
   */
  protected boolean saveSynchronously() {
    CRWriter crw = saveReport();
    if (crw == null)
      return false;
    while (crw.savingInProgress()) {
      try {
        this.wait(500);
      } catch (Exception e) {
      }
    }
    return true;
  }

  /**
   * Tries to determine the correct file type for the current data and starts saving it, if
   * successful. Saving is done in a new thread.
   * 
   * @return The writer that has started saving in another thread. You can check progress with
   *         {@link CRWriter#savingInProgress()}.
   */
  protected CRWriter saveReport() {
    FileType filetype = getData().getFileType();
    if (filetype == null) {
      File file = FileSaveAsAction.getFile(this);
      if (file != null) {
        try {
          filetype = FileTypeFactory.singleton().createFileType(file, false);
        } catch (IOException e) {
          Client.log.error("could not open " + file + " for saving: " + e);
          return null;
        }
      }
    }
    if (filetype == null) {
      Client.log.error("Could not determine file for saving");
      return null;
    }

    return saveReport(filetype);
  }

  /**
   * Starts saving the current data to the given filetype.
   * 
   * @param filetype
   * @return The writer that has started saving in another thread. You can check progress with
   *         {@link CRWriter#savingInProgress()}.
   */
  public CRWriter saveReport(FileType filetype) {
    CRWriter crw = null;
    try {

      // log.info("debugging: doSaveAction (FileType) called for FileType: " + filetype.toString());
      // write cr to file
      Client.log.info("Client.saveReport Using encoding: " + getData().getEncoding());
      ProgressBarUI ui = new ProgressBarUI(this);
      crw =
          new CRWriter(getData(), ui, filetype, getData().getEncoding(), Integer
              .parseInt(getProperties().getProperty("Client.CRBackups.count",
                  FileBackup.DEFAULT_BACKUP_LEVEL + "")));
      crw.writeAsynchronously();
      crw.close();

      // everything worked fine, so reset reportchanged state and also store new FileType settings
      setReportChanged(false);
      getData().setFileType(filetype);
      getData().resetToUnchanged();
      updateTitleCaption();
      getProperties().setProperty("Client.lastCRSaved", filetype.getName());
    } catch (ReadOnlyException exc) {
      Client.log.error(exc);
      JOptionPane.showMessageDialog(this, Resources.getFormatted(
          "actions.filesaveasaction.msg.filesave.readonly", filetype.getName()), Resources
          .get("actions.filesaveasaction.msg.filesave.error.title"), JOptionPane.ERROR_MESSAGE);
    } catch (IOException exc) {
      Client.log.error(exc);
      JOptionPane.showMessageDialog(this, exc.toString(), Resources
          .get("actions.filesaveasaction.msg.filesave.error.title"), JOptionPane.ERROR_MESSAGE);
    }
    return crw;
  }

  /**
   * This method should be called before the application is terminated in order to store GUI
   * settings etc.
   * 
   * @param storeSettings store the settings to magellan.ini if <code>storeSettings</code> is
   *          <code>true</code>.
   */
  public void quit(final boolean storeSettings) {
    final ProgressBarUI ui = new ProgressBarUI(this);
    final int response;
    if (reportState != null && reportState.isStateChanged()) {
      response = askToSave();
    } else {
      response = JOptionPane.NO_OPTION;
    }

    if (response == JOptionPane.CANCEL_OPTION)
      return;

    ui.show();
    new Thread(new Runnable() {

      public void run() {
        if (response == JOptionPane.CANCEL_OPTION) {
          ui.ready();
          return; // cancel or exception
        } else if (response == JOptionPane.YES_OPTION) {
          saveSynchronously();
        }
        ui.ready();

        for (MagellanPlugIn plugIn : getPlugIns()) {
          plugIn.quit(storeSettings);
        }

        saveExtendedState();
        setVisible(false);

        if (panels != null) {
          for (JPanel jPanel : panels) {
            InternationalizedDataPanel p = (InternationalizedDataPanel) jPanel;
            p.quit();
          }
        }

        NameGenerator.quit();

        if (fileHistory != null) {
          fileHistory.storeFileHistory();
        }

        // store settings to file
        if (storeSettings) {
          // save the desktop
          desktop.save();

          try {
            // if necessary, use settings file in local directory
            File settingsFile = new File(Client.settingsDirectory, "magellan.ini");

            if (settingsFile.exists() && settingsFile.canWrite()) {
              try {
                File backup = FileBackup.create(settingsFile);
                Client.log.info("Created backupfile " + backup);
              } catch (IOException ie) {
                Client.log.warn("Could not create backupfile for file " + settingsFile);
              }
            }

            if (settingsFile.exists() && !settingsFile.canWrite())
              throw new IOException("cannot write " + settingsFile);
            else {
              Client.log.info("Storing Magellan configuration to " + settingsFile);

              getProperties().store(new FileOutputStream(settingsFile), "");
            }
          } catch (IOException ioe) {
            Client.log.error(ioe);
          }
        }
        System.exit(0);
      }
    }).start();
  }

  /**
   * Sets the name of the current loaded data file.
   */
  // public void setDataFile(File file) {
  // this.dataFile = file;
  // }
  /**
   * Returns the name of the current loaded data file. If the result is null - then this does not
   * mean, that there is no report loaded - but not correctly set...
   */
  // public File getDataFile() {
  // return dataFile;
  // }
  // //////////////////
  // GAME DATA Code //
  // //////////////////
  /**
   * Loads game data from a file and returns it.
   * 
   * @param ui
   * @param fileName
   * @return the game data read or <code>null</code> if something went wrong
   */
  public GameData loadCR(UserInterface ui, File fileName) {
    GameData data = null;
    Client client = this;
    if (ui == null) {
      ui = new NullUserInterface();
      // ProgressBarUI(client);
    }

    try {
      ui.setMaximum(-1);
      ui.show();
      // FIXME(stm) maybe not pass ui to the reader here!?!
      data =
          new GameDataReader(ui).readGameData(FileTypeFactory.singleton().createFileType(fileName,
              true, new ClientFileTypeChooser(client)));
      if (data == null)
        throw new NullPointerException();
    } catch (FileTypeFactory.NoValidEntryException e) {
      ui.ready();
      JOptionPane.showMessageDialog(client, Resources.get("client.msg.loadcr.missingcr.text.1")
          + fileName + Resources.get("client.msg.loadcr.missingcr.text.2"), Resources
          .get("client.msg.loadcr.error.title"), JOptionPane.ERROR_MESSAGE);
      return null;
    } catch (FileNotFoundException exc) {
      ui.ready();
      JOptionPane.showMessageDialog(client, Resources.get("client.msg.loadcr.error.text")
          + exc.toString(), Resources.get("client.msg.loadcr.error.title"),
          JOptionPane.ERROR_MESSAGE);
      Client.log.info(exc);
      return null;
    } catch (Exception exc) {
      ui.ready();
      JOptionPane.showMessageDialog(client, Resources.get("client.msg.loadcr.error.text")
          + exc.toString(), Resources.get("client.msg.loadcr.error.title"),
          JOptionPane.ERROR_MESSAGE);
      Client.log.warn(exc);
      return null;
    }

    if (data.outOfMemory) {
      JOptionPane.showMessageDialog(client, Resources.get("client.msg.outofmemory.text"), Resources
          .get("client.msg.outofmemory.title"), JOptionPane.ERROR_MESSAGE);
      Client.log.error(Resources.get("client.msg.outofmemory.text"));
    }
    if (!MemoryManagment.isFreeMemory(data.estimateSize())) {
      JOptionPane.showMessageDialog(client, Resources.get("client.msg.lowmem.text"), Resources
          .get("client.msg.lowmem.title"), JOptionPane.WARNING_MESSAGE);
    }

    return data;
  }

  /**
   * This method asynchronously loads a CR into the client. Modality is ensured via a
   * {@link UserInterface}.
   * 
   * @param fileName The file name to be loaded.
   */
  public void loadCRThread(final File fileName) {
    loadCRThread(false, fileName);
  }

  /**
   * This method asynchronously loads a CR into the client. Modality is ensured via a
   * {@link UserInterface}.
   * 
   * @param saveFirst If <code>true</code>, this method attempts to first save the current data.
   * @param fileName The file name to be loaded.
   */
  public void loadCRThread(final boolean saveFirst, final File fileName) {

    final UserInterface ui = new ProgressBarUI(this);
    ui.show();

    new Thread(new Runnable() {
      public void run() {
        Client client = Client.INSTANCE;

        // save old data
        if (saveFirst) {
          saveSynchronously();
        }

        GameData data = null;

        data = loadCR(ui, fileName);

        if (data != null) {
          client.setData(data);
          client.setReportChanged(false);

          if (client.getSelectedObjects() != null) {
            client.getDispatcher().fire(client.getSelectedObjects());
          }
          // if we have active Region, center on it
          Region activeRegion = data.getActiveRegion();
          if (activeRegion != null) {
            client.getDispatcher().fire(SelectionEvent.create(client, activeRegion));
          } else {
            // suggestion by enno...if we have no active region but we have 0,0..center on 0,0
            CoordinateID cID = CoordinateID.ZERO;
            activeRegion = data.getRegion(cID);
            if (activeRegion != null) {
              client.getDispatcher().fire(SelectionEvent.create(client, activeRegion));
            }
          }
        }
      }
    }, "loadCRThread").start();

    /* this is here just for debugging reasons */
    if (false) {
      saveSynchronously();
    }
  }

  /**
   * Sets the origin of this client's data to newOrigin.
   * 
   * @param newOrigin The region in the GameData that is going to be the new origin
   */
  public void setOrigin(CoordinateID newOrigin) {
    GameData newData = null;
    try {
      newData = getData().clone(newOrigin);
      if (newData == null)
        throw new NullPointerException();
      if (newData.outOfMemory) {
        JOptionPane.showMessageDialog(this, Resources.get("client.msg.outofmemory.text"), Resources
            .get("client.msg.outofmemory.title"), JOptionPane.ERROR_MESSAGE);
        Client.log.error(Resources.get("client.msg.outofmemory.text"));
      }
    } catch (final CloneNotSupportedException e) {
      throw new RuntimeException("cannot happen");
    }
    if (!MemoryManagment.isFreeMemory(newData.estimateSize())) {
      JOptionPane.showMessageDialog(this, Resources.get("client.msg.lowmem.text"), Resources
          .get("client.msg.lowmem.title"), JOptionPane.WARNING_MESSAGE);
    }

    // FIXME(stm) do not change on out of memory!?
    setData(newData);
    setReportChanged(false);
  }

  public void setGirth(BBoxes newBorders) {
    // TODO compare with known borders
    for (Integer layer : newBorders.getLayers()) {
      BBox box = newBorders.getBox(layer);
      if (box.minx == box.maxx) {
        box.minx = Integer.MAX_VALUE;
        box.maxx = Integer.MIN_VALUE;
      }
      if (box.miny == box.maxy) {
        box.miny = Integer.MAX_VALUE;
        box.maxy = Integer.MIN_VALUE;
      }
    }

    GameData newData = GameDataMerger.merge(getData(), new BoxTransformer(newBorders));
    if (newData == null)
      throw new NullPointerException();
    if (newData.outOfMemory) {
      JOptionPane.showMessageDialog(this, Resources.get("client.msg.outofmemory.text"), Resources
          .get("client.msg.outofmemory.title"), JOptionPane.ERROR_MESSAGE);
      Client.log.error(Resources.get("client.msg.outofmemory.text"));
    }
    // FIXME(stm) do not change on out of memory!?
    setData(newData);
    setReportChanged(false);
  }

  /**
   * Callbacks of FileTypeFactory are handled by this object. Right now it returns the first
   * ZipEntry to mimic old cr loading behaviour for zip files.
   */
  private static class ClientFileTypeChooser extends FileTypeFactory.FileTypeChooser {
    Client client;

    /**
     * Creates a new ClientFileTypeChooser object.
     * 
     * @param client the parent Client object
     */
    public ClientFileTypeChooser(Client client) {
      this.client = client;
    }

    /**
     * open selection window to choose a zipentry
     * 
     * @see magellan.library.io.file.FileTypeFactory.FileTypeChooser#chooseZipEntry(java.util.zip.ZipEntry[])
     */
    @Override
    public ZipEntry chooseZipEntry(ZipEntry entries[]) {
      String stringEntries[] = new String[entries.length];

      for (int i = 0; i < entries.length; i++) {
        stringEntries[i] = entries[i].toString();
      }

      Object selected =
          JOptionPane.showInputDialog(client.getRootPane(), Resources
              .get("client.msg.loadcr.multiplezipentries.text"), Resources
              .get("client.msg.loadcr.multiplezipentries.title"), JOptionPane.QUESTION_MESSAGE,
              null, stringEntries, stringEntries[0]);

      if (selected == null)
        return null;

      for (ZipEntry entrie : entries) {
        if (selected.equals(entrie.toString()))
          return entrie;
      }

      return null;
    }
  }

  /**
   * Do some additional checks after loading a report.
   * 
   * @param aData the currently loaded game data
   */
  private void postProcessLoadedCR(GameData aData) {
    // show a warning if no password is set for any of the privileged
    // factions
    boolean factionsWithoutPassword = true;

    if (aData != null) {
      if (aData.getFactions() == null || aData.getFactions().size() == 0) {
        factionsWithoutPassword = false;
      }
    }

    if ((aData != null) && (aData.getFactions() != null)) {
      for (Faction f : aData.getFactions()) {

        if (f.getPassword() == null) {
          // take password from settings but only if it is not an
          // empty string
          String pwd =
              getProperties().getProperty("Faction.password." + (f.getID()).intValue(), null);

          if ((pwd != null) && !pwd.equals("")) {
            f.setPassword(pwd);
          }
        }

        // now check whether this faction has a password and eventually
        // set Trustlevel
        if ((f.getPassword() != null) && !f.isTrustLevelSetByUser()) {
          f.setTrustLevel(Faction.TL_PRIVILEGED);
        }

        if (f.getPassword() != null) {
          factionsWithoutPassword = false;
        }

        // check messages whether the password was changed
        if (f.getMessages() != null) {
          for (Message m : f.getMessages()) {

            // check message id (new and old)
            if ((m.getMessageType() != null)
                && (((m.getMessageType().getID()).intValue() == 1784377885) || ((m.getMessageType()
                    .getID()).intValue() == 19735))) {
              // this message indicates that the password has been
              // changed
              if (m.getAttributes() != null) {
                String value = m.getAttributes().get("value");

                // if the password in the message is valid and
                // does not match
                // the password already set anyway set it for
                // the faction and in the settings
                if (value != null) {
                  String password = value;

                  if (!password.equals("") && !password.equals(f.getPassword())) {
                    // ask user for confirmation to take new
                    // password from message
                    Object msgArgs[] = { f.toString() };

                    if (JOptionPane.showConfirmDialog(getRootPane(), (new java.text.MessageFormat(
                        Resources.get("client.msg.postprocessloadedcr.acceptnewpassword.text")))
                        .format(msgArgs), Resources
                        .get("client.msg.postprocessloadedcr.acceptnewpassword.title"),
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                      f.setPassword(password);

                      if (!f.isTrustLevelSetByUser()) { // password
                        // set
                        f.setTrustLevel(Faction.TL_PRIVILEGED);
                      }

                      factionsWithoutPassword = false;

                      if (getProperties() != null) {
                        getProperties().setProperty("Faction.password." + (f.getID()).intValue(),
                            f.getPassword());
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      // recalculate default-trustlevels after CR-Load
      TrustLevels.recalculateTrustLevels(aData);

      if (factionsWithoutPassword) { // no password set for any faction
        // okay, let's ask if there is a passwort to set.
        AskForPasswordDialog dialog = new AskForPasswordDialog(this, aData);
        dialog.setVisible(true);
      }

      // recalculate the status of regions - coastal or not?
      Regions.calculateCoastBorders(aData);
    }
  }

  // ///////////////
  // UPDATE Code //
  // ///////////////
  private void updateTitleCaption() {
    String title = createTitle(getData(), showStatus, false);

    try {
      title = createTitle(getData(), showStatus, true);
    } catch (Exception e) {
      Client.log.error("createTitle failed!", e);
    }

    setTitle(title);
  }

  private String createTitle(GameData data, boolean showStatusOverride, boolean longTitle) {
    // set frame title (date)
    StringBuilder title = new StringBuilder("Magellan");

    String version = VersionInfo.getVersion(Client.filesDirectory);

    if (version != null) {
      title.append(" ").append(version);
    }

    // pavkovic 2002.05.7: data may be null in this situation
    if (data == null)
      return title.toString();

    if (data.getFileType() != null) {
      String file;

      try {
        file = data.getFileType().getFile().toString();
      } catch (IOException e) {
        file = data.getFileType().toString();
      }

      file = file.substring(file.lastIndexOf(File.separator) + 1);
      title.append(" [").append(file).append("]");
    }

    if (data.getOwnerFaction() != null) {
      title.append(" - ").append(data.getOwnerFaction().toString());
    }

    if (data.getDate() != null) {
      title.append(" - ").append(
          data.getDate().toString(
              showStatusOverride ? Date.TYPE_SHORT : Date.TYPE_PHRASE_AND_SEASON)).append(" (")
          .append(data.getDate().getDate()).append(")");
    }

    if (!longTitle)
      return title.toString();

    if (showStatusOverride) {
      int units = 0;
      int done = 0;

      for (Unit u : data.getUnits()) {
        if (u.getFaction().isPrivileged()) {
          units++;

          if (u.isOrdersConfirmed()) {
            done++;
          }
        }

        // also count temp units
        for (Iterator<TempUnit> iter2 = u.tempUnits().iterator(); iter2.hasNext();) {
          Unit u2 = iter2.next();

          if (u2.getFaction().isPrivileged()) {
            units++;

            if (u2.isOrdersConfirmed()) {
              done++;
            }
          }
        }
      }

      if (units > 0) {
        BigDecimal percent =
            (new BigDecimal((done * 100) / ((float) units))).setScale(2, BigDecimal.ROUND_DOWN);
        title.append(" (").append(units).append(" ").append(Resources.get("client.title.unit"))
            .append(", ").append(done).append(" ").append(Resources.get("client.title.done"))
            .append(", ").append(Resources.get("client.title.thatare")).append(" ").append(percent)
            .append(" ").append(Resources.get("client.title.percent")).append(")");
      }
    }

    return title.toString();
  }

  /**
   * Updates the order confirmation menu after the game data changed.
   */
  private void updateConfirmMenu() {
    refillChangeFactionConfirmation(factionOrdersMenu,
        ChangeFactionConfirmationAction.SETCONFIRMATION);
    refillChangeFactionConfirmation(factionOrdersMenuNot,
        ChangeFactionConfirmationAction.REMOVECONFIRMATION);
    refillChangeFactionConfirmation(invertAllOrdersConfirmation,
        ChangeFactionConfirmationAction.INVERTCONFIRMATION);
  }

  /**
   * Updates the plugins after GameData Change
   */
  private void updatePlugIns() {
    if (plugIns != null && plugIns.size() > 0) {
      for (MagellanPlugIn plugIn : plugIns) {
        try {
          plugIn.init(getData());
        } catch (Throwable t) {
          ErrorWindow errorWindow = new ErrorWindow(this, t.getMessage(), "", t);
          errorWindow.setVisible(true);
        }
      }
    }
  }

  /**
   * Called after GameData changes. Also called via EventDispatcher thread to ensure graphical
   * changes do occur.
   */
  private void updatedGameData() {
    updateTitleCaption();
    updateConfirmMenu();
    updatePlugIns();

    if (getData().getCurTempID() == -1) {
      String s = getProperties().getProperty("ClientPreferences.TempIDsInitialValue", "");

      try {
        getData().setCurTempID("".equals(s) ? 0 : Integer.parseInt(s, getData().base));
      } catch (java.lang.NumberFormatException nfe) {
      }
    }

    // pavkovic 2004.01.04:
    // this method behaves at if the gamedata has been loaded by this
    // method.
    // this is not true at all but true enough for our needs.
    // dispatcher.fire(new GameDataEvent(this, data));
    getDispatcher().fire(new GameDataEvent(this, getData(), true));
    // also inform system about the new selection found in the GameData
    // object
    getDispatcher().fire(
        SelectionEvent.create(this, getData().getSelectedRegionCoordinates().values()));
    getDispatcher().fire(SelectionEvent.create(this, getData().getActiveRegion()));

  }

  // ////////////
  // L&F Code //
  // ////////////
  /**
   * @param laf
   */
  public void setLookAndFeel(String laf) {
    boolean lafSet = true;

    if (MagellanLookAndFeel.equals(laf)
        && laf.equals(getProperties().getProperty(PropertiesHelper.CLIENT_LOOK_AND_FEEL, ""))) {
      lafSet = false;
    } else {
      lafSet = MagellanLookAndFeel.setLookAndFeel(laf);

      if (!lafSet) {
        laf = "Metal";
        lafSet = MagellanLookAndFeel.setLookAndFeel("Metal");
      }
    }

    if (laf.equals("Metal")) {
      MagellanLookAndFeel.loadBackground(getProperties());
    }

    if (!lafSet)
      return;

    updateLaF();

    getProperties().setProperty(PropertiesHelper.CLIENT_LOOK_AND_FEEL, laf);
  }

  /**
   * DOCUMENT-ME
   */
  public void updateLaF() {
    // call updateUI in MagellanDesktop
    if (desktop != null) {
      desktop.updateLaF();
    }

    // call updateUI on preferences
    if (optionAction != null) {
      optionAction.updateLaF();
    }
  }

  private void initLookAndFeels() {
    setLookAndFeel(getProperties().getProperty(PropertiesHelper.CLIENT_LOOK_AND_FEEL, "Metal"));
  }

  /**
   * DOCUMENT-ME
   */
  public String[] getLookAndFeels() {
    return MagellanLookAndFeel.getLookAndFeelNames().toArray(new String[] {});
  }

  // ///////////////////
  // HISTORY methods //
  // ///////////////////

  /**
   * Adds a single file to the file history.
   * 
   * @param f
   */
  public void addFileToHistory(File f) {
    fileHistory.addFileToHistory(f);
  }

  /**
   * Returns the maximum number of entries in the history of loaded files.
   */
  public int getMaxFileHistorySize() {
    return fileHistory.getMaxFileHistorySize();
  }

  /**
   * Allows to set the maximum number of files appearing in the file history.
   * 
   * @param size
   */
  public void setMaxFileHistorySize(int size) {
    fileHistory.setMaxFileHistorySize(size);
  }

  // ///////////////////
  // PROPERTY Access //
  // ///////////////////
  /**
   * Changes to the report state can be done here. Normally, a change is recognized by the following
   * events.
   * 
   * @param changed
   */
  public void setReportChanged(boolean changed) {
    if (changed == false) {
      // for call from FileSaveAsAction
      updateTitleCaption();
    }

    reportState.setStateChanged(changed);
  }

  /**
   * Returns true if the report has changed since last save/load.
   */
  public boolean isReportChanged() {
    return reportState.isStateChanged();
  }

  /**
   * Get the selected Regions. The returned map can be empty but is never null. This is a wrapper
   * function so we dont need to give away MapperPanel.
   */
  public Map<CoordinateID, Region> getSelectedRegions() {
    return mapPanel.getSelectedRegions();
  }

  /**
   * Get the Level on the mapper Panel. This is a wrapper function so we dont need to give away
   * MapperPanel.
   */
  public int getLevel() {
    return mapPanel.getLevel();
  }

  // //////////////////////////
  // GENERAL ACCESS METHODS //
  // //////////////////////////

  /**
   * Returns the global settings used by Magellan.
   */
  public Properties getProperties() {
    if (context == null)
      return null;
    return context.getProperties();
  }

  /**
   * Sets a new GameData and notifies all game data listeners.
   * 
   * @param newData
   */
  public void setData(GameData newData) {
    context.setGameData(newData);
    postProcessLoadedCR(newData);

    // postProceddTheVoid moved to GameData.postProcess:
    if (newData != null
        && !PropertiesHelper.getBoolean(getProperties(), "map.creating.void", false)) {
      newData.removeTheVoid();
    }

    getDispatcher().fire(new GameDataEvent(this, newData));

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Client.this.updatedGameData();
      }
    });
  }

  /**
   * Returns the current GameData.
   */
  public GameData getData() {
    return context.getGameData();
  }

  /**
   * 
   */
  public MagellanDesktop getDesktop() {
    return desktop;
  }

  /**
   * Returns the current event dispatcher.
   */
  public EventDispatcher getDispatcher() {
    return context.getEventDispatcher();
  }

  /**
   * Returns the directory where the binaries are.
   */
  public static File getBinaryDirectory() {
    return Client.binDirectory;
  }

  /**
   * Returns the directory the local copy of Magellan is inside.
   */
  public static File getMagellanDirectory() {
    return Client.filesDirectory;
  }

  /**
   * Returns the directory for the Magellan settings.
   */
  public static File getSettingsDirectory() {
    return Client.settingsDirectory;
  }

  /**
   * @return the BookmarkManager associated with this Client-Object
   */
  public BookmarkManager getBookmarkManager() {
    return bookmarkManager;
  }

  /**
   * Returns <code>true</code> if order status should be shown in title.
   */
  public boolean isShowingStatus() {
    return showStatus;
  }

  /**
   * Changes the progress display behaviour.
   * 
   * @param bool If <code>true</code>, the progress is shown in the window title
   */
  public void setShowStatus(boolean bool) {
    if (showStatus != bool) {
      showStatus = bool;
      getProperties().setProperty("Client.ShowOrderStatus", showStatus ? "true" : "false");

      if (getData() != null) {
        updateTitleCaption();
      }
    }
  }

  // /////////////////////////////
  // REPAINT & VISIBILITY Code //
  // /////////////////////////////
  /**
   * @param v
   */
  public void setAllVisible(boolean v) {
    desktop.setAllVisible(v);
    resetExtendedState();
  }

  private void saveExtendedState() {
    if (getProperties() == null)
      return;
    getProperties().setProperty("Client.extendedState",
        String.valueOf(JVMUtilities.getExtendedState(this)));
  }

  private void resetExtendedState() {
    int state = new Integer(getProperties().getProperty("Client.extendedState", "-1")).intValue();

    if (state != -1) {
      JVMUtilities.setExtendedState(this, state);
    }
  }

  // The repaint functions are overwritten to repaint the whole Magellan
  // Desktop. This is necessary because of the desktop mode FRAME.
  /**
   * @see java.awt.Component#repaint()
   */
  @Override
  public void repaint() {
    super.repaint();

    if (desktop != null) {
      desktop.repaintAllComponents();
    }
  }

  /**
   * Repaints all components.
   * 
   * @param millis maximium time in milliseconds before update
   * @see Component#repaint(long)
   */
  public void repaint(int millis) {
    super.repaint(millis);

    if (desktop != null) {
      desktop.repaintAllComponents();
    }
  }

  // /////////////////
  // SHORTCUT Code //
  // /////////////////

  /**
   * Empty because registered directly.
   */
  public Iterator<KeyStroke> getShortCuts() {
    return null; // not used - we register directly with a KeyStroke
  }

  /**
   * Repaints the client.
   * 
   * @see magellan.client.desktop.ShortcutListener#shortCut(javax.swing.KeyStroke)
   */
  public void shortCut(javax.swing.KeyStroke shortcut) {
    desktop.repaintAllComponents();
  }

  /**
   * @see magellan.client.desktop.ShortcutListener#getShortcutDescription(javax.swing.KeyStroke)
   */
  public String getShortcutDescription(KeyStroke stroke) {
    return Resources.get("client.shortcut.description");
  }

  /**
   * @see magellan.client.desktop.ShortcutListener#getListenerDescription()
   */
  public String getListenerDescription() {
    return Resources.get("client.shortcut.title");
  }

  /**
   * Returns an adapter for the preferences of this class.
   */
  public PreferencesAdapter createPreferencesAdapter() {
    return new ClientPreferences(getProperties(), this);
  }

  // /////////////////
  // INNER Classes //
  // /////////////////
  private class MenuActionObserver implements PropertyChangeListener {
    protected JMenuItem item;

    /**
     * Creates a new MenuActionObserver object.
     * 
     * @param item DOCUMENT-ME
     * @param action DOCUMENT-ME
     */
    public MenuActionObserver(JMenuItem item, Action action) {
      this.item = item;
      action.addPropertyChangeListener(this);
    }

    /**
     * DOCUMENT-ME
     * 
     * @param e DOCUMENT-ME
     */
    public void propertyChange(PropertyChangeEvent e) {
      if ((e.getPropertyName() != null) && e.getPropertyName().equals("accelerator")) {
        item.setAccelerator((KeyStroke) e.getNewValue());
      }
    }
  }

  /**
   * Simple class to look for events changing the data.
   */
  protected class ReportObserver implements GameDataListener, OrderConfirmListener,
      TempUnitListener, UnitOrdersListener {
    protected boolean stateChanged = false;

    protected long lastClear;

    /**
     * Creates a new ReportObserver object.
     */
    public ReportObserver(EventDispatcher e) {

      e.addGameDataListener(this);
      e.addOrderConfirmListener(this);
      e.addTempUnitListener(this);
      e.addUnitOrdersListener(this);

      lastClear = -1;
    }

    /**
     * Returns <code>true</code> if the report was changed.
     */
    public boolean isStateChanged() {
      return stateChanged;
    }

    /**
     * Changes the state to <code>newState</code>.
     */
    public void setStateChanged(boolean newState) {
      stateChanged = newState;

      if (!newState) {
        lastClear = System.currentTimeMillis();
      }
    }

    /**
     * Sets the state to changed if the event occurred after the last clear event.
     * 
     * @see magellan.client.event.OrderConfirmListener#orderConfirmationChanged(magellan.client.event.OrderConfirmEvent)
     */
    public void orderConfirmationChanged(OrderConfirmEvent e) {
      if ((getData() != null) && isShowingStatus()) {
        updateTitleCaption();
      }

      if (lastClear < e.getTimestamp()) {
        stateChanged = true;
      }
    }

    /**
     * Sets the state to changed if the event occurred after the last clear event.
     * 
     * @see magellan.client.event.TempUnitListener#tempUnitCreated(magellan.client.event.TempUnitEvent)
     */
    public void tempUnitCreated(TempUnitEvent e) {
      if (lastClear < e.getTimestamp()) {
        stateChanged = true;
      }
    }

    /**
     * Sets the state to changed if the event occurred after the last clear event.
     * 
     * @see magellan.client.event.TempUnitListener#tempUnitDeleting(magellan.client.event.TempUnitEvent)
     */
    public void tempUnitDeleting(TempUnitEvent e) {
      if (lastClear < e.getTimestamp()) {
        stateChanged = true;
      }
    }

    /**
     * Updates the caption and sets changed state if the event occurred after the last call of
     * <code>setChangedState(false)</code>.
     * 
     * @see magellan.library.event.GameDataListener#gameDataChanged(magellan.library.event.GameDataEvent)
     */
    public void gameDataChanged(GameDataEvent e) {
      if ((lastClear < e.getTimestamp()) && (e.getGameData() != null)) {
        stateChanged = true;
      } else {
        stateChanged = false;
      }
      updateTitleCaption();
    }

    /**
     * Sets the state to changed if the event occurred after the last clear event.
     * 
     * @see magellan.client.event.UnitOrdersListener#unitOrdersChanged(magellan.client.event.UnitOrdersEvent)
     */
    public void unitOrdersChanged(UnitOrdersEvent e) {
      if (lastClear < e.getTimestamp()) {
        stateChanged = true;
      }
    }
  }

  public SelectionEvent getSelectedObjects() {
    return overviewPanel.getSelectedObjects();
  }

  /**
   * Returns a list of all loaded magellan plugins.
   */
  public Collection<MagellanPlugIn> getPlugIns() {
    return plugIns;
  }

  /**
   * This method tries to load all Magellan PlugIns.
   */
  public void initPlugIns() {
    MagellanPlugInLoader loader = new MagellanPlugInLoader();
    Properties properties = getProperties();
    // helper: store Magellan-Dir in properties toBe changed
    properties.setProperty("plugin.helper.magellandir", Client.filesDirectory.toString());
    List<Class<MagellanPlugIn>> plugInClasses =
        new ArrayList<Class<MagellanPlugIn>>(loader.getExternalModuleClasses(properties));
    Collections.sort(plugInClasses, new Comparator<Class<MagellanPlugIn>>() {

      public int compare(Class<MagellanPlugIn> o1, Class<MagellanPlugIn> o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });

    for (Class<MagellanPlugIn> plugInClass : plugInClasses) {
      try {
        MagellanPlugIn plugIn = plugInClass.newInstance();
        plugIn.init(this, properties);
        plugIns.add(plugIn);
      } catch (Throwable t) {
        ErrorWindow errorWindow = new ErrorWindow(this, t.getMessage(), "", t);
        errorWindow.setVisible(true);
      }
    }
  }

  /**
   * Returns a String representing all parts of the component for debugging.
   */
  public static String debug(Component comp) {
    String result = "";
    if (comp instanceof Container) {
      Container container = (Container) comp;
      result = "Container: " + container + "\n";
      result += "{";
      Component[] comps = container.getComponents();
      for (Component acomp : comps) {
        result += " " + Client.debug(acomp) + "\n";
      }
      result += ")";
    } else {
      result = "Component: " + comp + "\n";
    }
    return result;
  }

  /**
   * on windows-OS tries to locate the included ECheck.exe and if found save the path into
   * properties
   */
  public void initECheckPath(Properties settings) {
    // check if we have a windows os
    String osName = System.getProperty("os.name");
    osName = osName.toLowerCase();
    if (osName.indexOf("windows") > -1) {
      Client.log.info("new ini. windows OS detected. (" + osName + ")");
      // we have a windows OS
      // lets assume the location
      String actPath =
          Client.settingsDirectory + File.separator + "echeck" + File.separator + "ECheck.exe";
      Client.log.info("checking for ECheck: " + actPath);
      File echeckFile = new File(actPath);
      if (echeckFile.exists()) {
        // yep, we have an ECheck.exe here
        // lets add to the properties
        settings.setProperty("JECheckPanel.echeckEXE", echeckFile.toString());
        Client.log.info("set echeckEXE to: " + echeckFile.toString());
      } else {
        Client.log.info("ECheck.exe not found");
      }
    } else {
      Client.log.info("new ini. non - windows OS detected. (" + osName + ")");
    }
  }

}
