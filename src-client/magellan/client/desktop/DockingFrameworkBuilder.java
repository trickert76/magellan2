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

package magellan.client.desktop;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JSplitPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import magellan.client.Client;
import magellan.client.utils.ErrorWindow;
import magellan.library.utils.Encoding;
import magellan.library.utils.Resources;
import magellan.library.utils.Utils;
import magellan.library.utils.logging.Logger;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.FloatingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.WindowBar;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.StringViewMap;
import net.infonode.util.Direction;


/**
 *
 * @author Andreas
 * @author Thoralf
 * @version
 */
public class DockingFrameworkBuilder  {
  private static final Logger log = Logger.getInstance(DockingFrameworkBuilder.class);
	private List<Component> componentsUsed;
  private StringViewMap viewMap = null;

	/** Holds value of property screen. */
	private Rectangle screen;
	private static Dimension minSize;

	/**
	 * Creates new DockingFrameworkBuilder
	 *
	 * 
	 */
	public DockingFrameworkBuilder(Rectangle s) {
		componentsUsed = new LinkedList<Component>();
		screen = s;

		if(minSize == null) {
			minSize = new Dimension(100, 10);
		}
	}

	/**
	 * 
	 */
	public JComponent buildDesktop(FrameTreeNode root, Map<String,Component> components, File serializedView) {
		componentsUsed.clear();
		root = checkTree(root, components);

		if(root == null) {
			return null;
		}

		if(root.isLeaf()) {
			componentsUsed.add(components.get(root.getName()));

			return (JComponent) components.get(root.getName());
		}
    
    // okay, this is a try for InfoNode docking window framework
    // we have a tree of settings and a list of components. Let's build a root window here...
    
    return createRootWindow(components,screen,serializedView);

		//return createSplit(root, components, screen);
	}
  
  /**
   * This method tries to setup the infonode docking framework.
   */
  protected JComponent createRootWindow(Map<String,Component> components, Rectangle size, File serializedViewData) {
    Map<String,View> views = new HashMap<String,View>();
    viewMap = new StringViewMap();

    for (String key : components.keySet()) {
      if (key.equals("COMMANDS")) continue; // deprecated
      if (key.equals("NAME")) continue; // deprecated
      if (key.equals("DESCRIPTION")) continue; // deprecated
      if (key.equals("OVERVIEW&HISTORY")) continue; // deprecated
      
      Component component = components.get(key);
      
      View view = new View(Resources.get("dock."+key+".title"),null,component);
      view.setName(key);
      view.setToolTipText(Resources.get("dock."+key+".tooltip"));
      viewMap.addView(key,view);
      views.put(key,view);
    }
    
    RootWindow window = null;
    try {
      if (serializedViewData != null && serializedViewData.exists()) {
        window = read(viewMap,views,serializedViewData);
      } else {
        window = createDefault(viewMap,views);
      }
    } catch (NullPointerException npe) {
      // okay, sometimes this happens without a reason (setToolTipText())...
      log.error("NPE",npe);
    } catch (Throwable t) {
      log.fatal(t.getMessage(),t);
      ErrorWindow errorWindow = new ErrorWindow(Client.INSTANCE,t.getMessage(),"",t);
      errorWindow.setVisible(true);
    }
    
    DockingWindowsTheme theme = new ShapedGradientDockingTheme();
    window.getWindowBar(Direction.DOWN).setEnabled(true);
    window.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());
    window.setPopupMenuFactory(new MagellanPopupMenuFactory(viewMap));
    
    window.getRootWindowProperties().getWindowAreaProperties().setBackgroundColor(null).setBorder(null);
    window.getRootWindowProperties().getWindowAreaShapedPanelProperties().setComponentPainter(null);
    window.getRootWindowProperties().getComponentProperties().setBackgroundColor(null);
    window.getRootWindowProperties().getShapedPanelProperties().setComponentPainter(null);

    return window;
  }
  
  public StringViewMap getViewMap() {
    return viewMap;
  }
  
  /**
   * This method writes a docking configuration to the given file.
   */
  public void write(File serializedViewData, RootWindow window) throws IOException {
//    FileOutputStream fos = new FileOutputStream(serializedViewData);
//    ObjectOutputStream oos = new MagellanObjectOutputStream(fos);
//    window.write(oos, true);
//    oos.close();
//    fos.close();
    
    StringBuffer buffer = new StringBuffer();
    buffer.append("<?xml version='1.0' encoding='"+Encoding.DEFAULT.toString()+"'?>\r\n");
    buffer.append("<dock>\r\n");
    save(buffer,window,"");
    buffer.append("</dock>\r\n");
    
//    System.out.println(buffer);
    
    PrintWriter pw = new PrintWriter(serializedViewData,Encoding.DEFAULT.toString());
    pw.println(buffer.toString());
    pw.close();
  }
  
  protected synchronized void save(StringBuffer buffer, DockingWindow window, String offset) {
    if (window == null) return;
    if (window instanceof SplitWindow) {
      save(buffer,(SplitWindow)window,offset);
    } else if (window instanceof TabWindow) {
      save(buffer,(TabWindow)window,offset);
    } else if (window instanceof View) {
      save(buffer,(View)window,offset);
    } else if (window instanceof RootWindow) {
      save(buffer,(RootWindow)window,offset);
    } else if (window instanceof FloatingWindow) {
      save(buffer,(FloatingWindow)window,offset);
    } else if (window instanceof WindowBar) {
      save(buffer,(WindowBar)window,offset);
    } else {
      log.warn("UNKNOWN DockingWindow Type");
      log.warn("Title:"+window.getTitle());
      log.warn("Type.:"+window.getClass().getName());
      for (int i=0; i<window.getChildWindowCount(); i++) {
        save(buffer,window.getChildWindow(i),offset+"  ");
      }
    }
  }
  protected synchronized void save(StringBuffer buffer, SplitWindow window, String offset) {
    buffer.append(offset+"<splitwindow divider='"+window.getDividerLocation()+"' horizontal='"+window.isHorizontal()+"'>\r\n");
    for (int i=0; i<window.getChildWindowCount(); i++) {
      buffer.append(offset+" <split>\r\n");
      save(buffer,window.getChildWindow(i),offset+"  ");
      buffer.append(offset+" </split>\r\n");
    }
    buffer.append(offset+"</splitwindow>\r\n");
  }
  protected synchronized void save(StringBuffer buffer, RootWindow window, String offset) {
    buffer.append(offset+"<rootwindow>\r\n");
    for (int i=0; i<window.getChildWindowCount(); i++) {
      save(buffer,window.getChildWindow(i),offset+"  ");
    }
    buffer.append(offset+"</rootwindow>\r\n");
  }
  protected synchronized void save(StringBuffer buffer, TabWindow window, String offset) {
    buffer.append(offset+"<tabwindow>\r\n");
    for (int i=0; i<window.getChildWindowCount(); i++) {
      DockingWindow tab = window.getChildWindow(i);
      buffer.append(offset+" <tab isActive='"+window.getSelectedWindow().equals(tab)+"'>\r\n");
      save(buffer,tab,offset+"  ");
      buffer.append(offset+" </tab>\r\n");
    }
    buffer.append(offset+"</tabwindow>\r\n");
  }
  protected synchronized void save(StringBuffer buffer, View window, String offset) {
    buffer.append(offset+"<view title='"+Utils.escapeXML(window.getName())+"'>\r\n");
    for (int i=0; i<window.getChildWindowCount(); i++) {
      save(buffer,window.getChildWindow(i),offset+"  ");
    }
    buffer.append(offset+"</view>\r\n");
  }
  protected synchronized void save(StringBuffer buffer, WindowBar window, String offset) {
    buffer.append(offset+"<windowbar>\r\n");
    for (int i=0; i<window.getChildWindowCount(); i++) {
      save(buffer,window.getChildWindow(i),offset+"  ");
    }
    buffer.append(offset+"</windowbar>\r\n");
  }
  protected synchronized void save(StringBuffer buffer, FloatingWindow window, String offset) {
    buffer.append(offset+"<floatingwindow x='"+((int)window.getLocation().getX())+"' y='"+((int)window.getLocation().getY())+"' width='"+((int)window.getSize().getWidth())+"' height='"+((int)window.getSize().getHeight())+"'>\r\n");
    for (int i=0; i<window.getChildWindowCount(); i++) {
      save(buffer,window.getChildWindow(i),offset+"  ");
    }
    buffer.append(offset+"</floatingwindow>\r\n");
  }
  
  /**
   * This method reads a docking configuration from the given file.
   */
  public synchronized RootWindow read(StringViewMap viewMap, Map<String,View> views, File serializedViewData) throws IOException {
    RootWindow window = DockingUtil.createRootWindow(viewMap, true);
    
    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document document = builder.parse(serializedViewData);
      if (!document.getDocumentElement().getNodeName().equals("dock")) return null;
      load(window,viewMap,views,document.getDocumentElement());
    } catch (Exception e) {
      log.error(e);
    }
    
//    FileInputStream fis = new FileInputStream(serializedViewData);
//    ObjectInputStream ois = new MagellanObjectInputStream(fis);
//    window.read(ois, true);
//    ois.close();
//    fis.close();
    return window;
  }
  protected synchronized DockingWindow load(RootWindow window, StringViewMap viewMap, Map<String,View> views, Element root) {
    if (root.getNodeName().equalsIgnoreCase("dock")) {
      NodeList subnodes = root.getChildNodes();
      for (int i=0; i<subnodes.getLength(); i++) {
        Node node = subnodes.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          load(window,viewMap,views,(Element)node);
        }
      }
    } else if (root.getNodeName().equalsIgnoreCase("rootwindow")) {
      NodeList subnodes = root.getChildNodes();
      DockingWindow child = null;
      for (int i=0; i<subnodes.getLength(); i++) {
        Node node = subnodes.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          child = load(window,viewMap,views,(Element)node);
          if (child == null) continue;
          if (child instanceof FloatingWindow) continue;
          if (child instanceof WindowBar) continue;
          window.setWindow(child);
        }
      }
    } else if (root.getNodeName().equalsIgnoreCase("windowbar")) {
      // ...
    } else if (root.getNodeName().equalsIgnoreCase("floatingwindow")) {
      return loadFloatingWindow(window,viewMap,views,root);
    } else if (root.getNodeName().equalsIgnoreCase("splitwindow")) {
      return loadSplitWindow(window,viewMap,views,root);
    } else if (root.getNodeName().equalsIgnoreCase("tabwindow")) {
      return loadTabWindow(window,viewMap,views,root);
    } else if (root.getNodeName().equalsIgnoreCase("view")) {
      return loadView(window,viewMap,views,root);
    } else {
      NodeList subnodes = root.getChildNodes();
      for (int i=0; i<subnodes.getLength(); i++) {
        Node node = subnodes.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          return load(window,viewMap,views,(Element)node);
        }
      }
    }
    
    return null;
  }
  protected synchronized DockingWindow loadSplitWindow(RootWindow window, StringViewMap viewMap, Map<String,View> views, Element root) {
    boolean isHorizontal = Boolean.valueOf(root.getAttribute("horizontal"));
    float divider = Float.valueOf(root.getAttribute("divider"));
    List<Element> nodes = Utils.getChildNodes(root,"split");
    
    SplitWindow splitWindow = null;
    if (nodes.size()==2) {
      DockingWindow left = load(window,viewMap,views,Utils.getChildNode(nodes.get(0)));
      DockingWindow right = load(window,viewMap,views,Utils.getChildNode(nodes.get(1)));
      splitWindow = new SplitWindow(isHorizontal,left,right);
    } else {
      splitWindow = new SplitWindow(isHorizontal);
    }
    splitWindow.setDividerLocation(divider);
    
    return splitWindow;
  }
  protected synchronized DockingWindow loadTabWindow(RootWindow window, StringViewMap viewMap, Map<String,View> views, Element root) {
    List<Element> nodes = Utils.getChildNodes(root,"tab");
    
    TabWindow tabWindow = new TabWindow();
    int selected = 0;
    
    for (int i=0; i<nodes.size(); i++) {
      Element e = nodes.get(i);
        
      boolean isActive = Boolean.valueOf(e.getAttribute("isActive"));
      if (isActive) selected = i;
      
      DockingWindow tab = load(window,viewMap,views,Utils.getChildNode(e));
      if (tab == null) continue;
      tabWindow.addTab(tab);
    }
    
    tabWindow.setSelectedTab(selected);
    
    return tabWindow;
  }
  protected synchronized DockingWindow loadView(RootWindow window, StringViewMap viewMap, Map<String,View> views, Element root) {
    String key = root.getAttribute("title");
    View view = views.get(key);
    return view;
  }
  protected synchronized FloatingWindow loadFloatingWindow(RootWindow window, StringViewMap viewMap, Map<String,View> views, Element root) {
    DockingWindow child = null;
    NodeList subnodes = root.getChildNodes();
    for (int i=0; i<subnodes.getLength(); i++) {
      Node node = subnodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        child = load(window,viewMap,views,(Element)node);
        if (child != null) break;
      }
    }
    if (child == null) return null;
    int x = Utils.getIntValue(root.getAttribute("x"));
    int y = Utils.getIntValue(root.getAttribute("y"));
    int width = Utils.getIntValue(root.getAttribute("width"));
    int height = Utils.getIntValue(root.getAttribute("height"));
    
    FloatingWindow floatWindow = window.createFloatingWindow(new Point(x,y), new Dimension(width,height), child);
    return floatWindow;
  }
  
  
  protected synchronized RootWindow createDefault(StringViewMap viewMap, Map<String,View> views) {
    RootWindow window = DockingUtil.createRootWindow(viewMap, true);
    
    View overview = views.get("OVERVIEW");
    View history = views.get("HISTORY");
    View minimap = views.get("MINIMAP");
    View map = views.get("MAP");
    View messages = views.get("MESSAGES");
    View details = views.get("DETAILS");
    View orders = views.get("ORDERS");
    View name = views.get("NAME&DESCRIPTION");
    View echeck = views.get("ECHECK");
    
    TabWindow bottomLeft = new TabWindow(new DockingWindow[]{minimap,history});
    bottomLeft.setSelectedTab(0);
    SplitWindow left = new SplitWindow(false,overview,bottomLeft);
    left.setDividerLocation(0.6f);
    
    TabWindow bottomCenter = new TabWindow(new DockingWindow[]{messages,echeck});
    bottomCenter.setSelectedTab(0);
    SplitWindow middle = new SplitWindow(false,map,bottomCenter);
    middle.setDividerLocation(0.6f);
    
    SplitWindow topRight = new SplitWindow(false,name,details);
    SplitWindow right = new SplitWindow(false,topRight,orders);
    
    SplitWindow splitWindow = new SplitWindow(true,left,new SplitWindow(true,middle,right));
    splitWindow.setDividerLocation(0.3f);
    
    window.setWindow(splitWindow);
    
    return window;
  }
  
  public static JMenu createDesktopMenu(Map<String,Component> components, ActionListener listener) {
    JMenu desktopMenu = new JMenu(Resources.get("desktop.magellandesktop.menu.desktop.caption"));
    desktopMenu.setMnemonic(Resources.get("desktop.magellandesktop.menu.desktop.mnemonic").charAt(0));
    
    if(components.size() > 0) {
      for (String key : components.keySet()) {
        if (key.equals("COMMANDS")) continue; // deprecated
        if (key.equals("NAME")) continue; // deprecated
        if (key.equals("DESCRIPTION")) continue; // deprecated
        if (key.equals("OVERVIEW&HISTORY")) continue; // deprecated
        
        Component component = components.get(key);
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(Resources.get("dock."+key+".title"), false);
        item.setActionCommand("menu."+key);
        desktopMenu.add(item);
        item.addActionListener(listener);
      }
    }
    
    return desktopMenu;
  }
  
	protected FrameTreeNode checkTree(FrameTreeNode node, Map comp) {
		if(node == null) {
			return null;
		}

		if(node.isLeaf()) {
			if(comp.containsKey(node.getName())) {
				return node;
			} else {
				return null;
			}
		}

		FrameTreeNode left = checkTree(node.getChild(0), comp);
		FrameTreeNode right = checkTree(node.getChild(1), comp);
		node.setChild(0, left);
		node.setChild(1, right);

		if(left == null) {
			return right;
		}

		if(right == null) {
			return left;
		}

		return node;
	}

	protected JComponent createSplit(FrameTreeNode current, Map components, Rectangle sourceRect) {
		int orient = current.getOrientation();
		JSplitPane jsp = magellan.client.swing.ui.UIFactory.createBorderlessJSplitPane(orient);
		Rectangle left = new Rectangle();
		Rectangle right = new Rectangle();
		left.x = sourceRect.x;
		left.y = sourceRect.y;

		if(current.isAbsolute()) {
			int divider = (int) Math.round(current.getPercentage());
			divider = checkDividerInRectangle(divider, orient, sourceRect);
			jsp.setDividerLocation(divider);
			createRects(orient, divider, sourceRect, left, right);
		} else {
			int divider = createRects(orient, current.getPercentage(), sourceRect, left, right);
			jsp.setDividerLocation(divider);
		}

		// pavkovic 2004.04.02: remove one touch expander
		jsp.setOneTouchExpandable(false);

		// connect the split pane and the node
		current.connectToSplitPane(jsp);

		if(current.getChild(0).isLeaf()) {
			JComponent jc = (JComponent) components.get(current.getChild(0).getName());

            {
                Object name = current.getChild(0).getName();
                String configuration = current.getChild(0).getConfiguration();
                // special meaning of overview
                if("OVERVIEW".equals(name))  {
                    name = "OVERVIEW&HISTORY";
                } 
                if(components.get(name) instanceof Initializable && configuration != null) {
                    ((Initializable) components.get(name)).initComponent(configuration);
                }
            }

			jc.setMinimumSize(minSize);

			if(current.getChild(0).getName() == null) {
				jsp.setTopComponent(jc);
			} else {
				jsp.setTopComponent(new magellan.client.swing.ui.InternalFrame(current.getChild(0)
																				  .getName(), jc));
			}

			if(!componentsUsed.contains(jc)) {
				componentsUsed.add(jc);
			}
		} else {
			JComponent jc = createSplit(current.getChild(0), components, left);

			if(current.getChild(0).getName() == null) {
				jsp.setTopComponent(jc);
			} else {
				jsp.setTopComponent(new magellan.client.swing.ui.InternalFrame(current.getChild(0)
																				  .getName(), jc));
			}
		}

		if(current.getChild(1).isLeaf()) {
			JComponent jc = (JComponent) components.get(current.getChild(1).getName());

			if((jc instanceof Initializable) && (current.getChild(1).getConfiguration() != null)) {
				((Initializable) jc).initComponent(current.getChild(1).getConfiguration());
			}

			jc.setMinimumSize(minSize);

			if(current.getChild(1).getName() == null) {
				jsp.setBottomComponent(jc);
			} else {
				jsp.setBottomComponent(new magellan.client.swing.ui.InternalFrame(current.getChild(1)
																					 .getName(), jc));
			}

			if(!componentsUsed.contains(jc)) {
				componentsUsed.add(jc);
			}
		} else {
			// jsp.setBottomComponent(createSplit(current.getChild(1), components, right));
			JComponent jc = createSplit(current.getChild(1), components, right);

			if(current.getChild(1).getName() == null) {
				jsp.setBottomComponent(jc);
			} else {
				jsp.setBottomComponent(new magellan.client.swing.ui.InternalFrame(current.getChild(1)
																					 .getName(), jc));
			}
		}

		return jsp;
	}

	private void createRects(int orient, int divider, Rectangle source, Rectangle left,
							 Rectangle right) {
		if(orient == JSplitPane.HORIZONTAL_SPLIT) {
			left.width = divider - left.x;
			left.height = source.height;
			right.x = divider;
			right.y = left.y;
			right.width = source.width - left.width;
			right.height = left.height;
		} else {
			left.width = source.width;
			left.height = divider - source.y;
			right.x = source.x;
			right.y = divider;
			right.width = source.width;
			right.height = source.height - left.height;
		}
	}

	private int createRects(int orient, double div, Rectangle source, Rectangle left,
							Rectangle right) {
		int divider;

		if(orient == JSplitPane.HORIZONTAL_SPLIT) {
			divider = source.x + (int) (div * source.width);
			left.width = divider - left.x;
			left.height = source.height;
			right.x = divider;
			right.y = left.y;
			right.width = source.width - left.width;
			right.height = left.height;
			divider = left.width;
		} else {
			divider = source.y + (int) (div * source.height);
			left.width = source.width;
			left.height = divider - source.y;
			right.x = source.x;
			right.y = divider;
			right.width = source.width;
			right.height = source.height - left.height;
			divider = left.height;
		}

		return divider;
	}

	private int checkDividerInRectangle(int divider, int orient, Rectangle bounds) {
		if(orient == JSplitPane.HORIZONTAL_SPLIT) {
			if(divider < 0) {
				return 1;
			}

			if(divider > bounds.width) {
				return bounds.width - 1;
			}
		} else {
			if(divider < 0) {
				return 1;
			}

			if(divider > bounds.height) {
				return bounds.height - 1;
			}
		}

		return divider;
	}

	/**
	 * 
	 */
	public List getComponentsUsed() {
		return componentsUsed;
	}

	/**
	 * Getter for property screen.
	 *
	 * @return Value of property screen.
	 */
	public Rectangle getScreen() {
		return screen;
	}

	/**
	 * Setter for property screen.
	 *
	 * @param screen New value of property screen.
	 */
	public void setScreen(Rectangle screen) {
		this.screen = screen;
	}
}
