package com.toomasr.sgf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toomasr.sgf4j.parser.Game;
import com.toomasr.sgf4j.parser.GameNode;

public class Parser {
  private static final Logger log = LoggerFactory.getLogger(Parser.class);
  private final String originalGame;

  // http://www.red-bean.com/sgf/properties.html
  private static final Set<String> generalProps = new HashSet<String>();
/*
  // I think - Black Player name
  generalProps.add("PX");
  // I think - White Player name
  generalProps.add("PY");
    // KGSDE - kgs scoring - marks all prisoner stones
    // http://senseis.xmp.net/?CgobanProblemsAndSolutions
    generalProps.add("KGSDE");
    // KGS - score white KGS 得分
    generalProps.add("KGSSW");
    // KGS - score black KGS 得分
    generalProps.add("KGSSB");
    // Checkmark - ignored in FF4
    // http://www.red-bean.com/sgf/ff1_3/ff3.html and http://www.red-bean.com/sgf/changes.html
    generalProps.add("CH");
    // I have no idea what these properties means
    // but they are in many games of the collections
    // I've downloaded from the interwebs
    generalProps.add("OH");
    generalProps.add("LC");
    generalProps.add("RD"); // maybe release date?
    generalProps.add("TL"); // something to do with time
    generalProps.add("GK"); // something to do with the game
  // "LT": enforces losing on time LT[]
  // http://www.red-bean.com/sgf/ff1_3/ff3.html
  // I don't get it but I'm parsing it
  generalProps.add("LT");
  */
  static {
    // Application used to generate the SGF 应用
    generalProps.add("AP");
    // Black's Rating 段位
    generalProps.add("BR");
    // White's Rating 段位
    generalProps.add("WR");
    // KOMI 贴目
    generalProps.add("KM");
    // Black Player Extended information 信息
    generalProps.add("PBX");
    // Black Player name 姓名
    generalProps.add("PB");
    // White Player name 姓名
    generalProps.add("PW");
    // Charset 字符编码
    generalProps.add("CA");
    // File format 格式版本有四种
    generalProps.add("FF");
    // Game type - 1 means Go 游戏各类，默认为围棋，也可以是其它棋
    generalProps.add("GM");
    // Size of the board 棋盘
    generalProps.add("SZ");
    // Annotator 注释
    generalProps.add("AN");
    // Name of the event EV赛事
    generalProps.add("EV");
    // Name of the event extended
    // Extended info about the event 赛事
    generalProps.add("EVX");
    // Rount number 回合
    generalProps.add("RO");
    // Rules 规则
    generalProps.add("RU");
    // Time limit in seconds 限时
    generalProps.add("TM");
    // How overtime is handled 用时
    generalProps.add("OT");
    // Date of the game 日期
    generalProps.add("DT");
    // Extended date 日期扩展
    generalProps.add("DTX");
    // Place of the game 地址
    generalProps.add("PC");
    // Result of the game 结果
    generalProps.add("RE");
    // I think - Result of the game
    generalProps.add("ER");
    // How to show comments 显示信息的方式
    generalProps.add("ST");
    /*
     * Provides some extra information about the following game.
     * The intend of GC is to provide some background information
     * and/or to summarize the game itself. 备注
     */
    generalProps.add("GC");
    // Any copyright information 版权
    generalProps.add("CP");
    // Provides name of the source 来源
    generalProps.add("SO");
    // Name of the white team 队
    generalProps.add("WT");
    // Name of the black team 队
    generalProps.add("BT");
    // name of the user or program who entered the game 编者
    generalProps.add("US");
    // How to print move numbers 打印
    generalProps.add("PM");
    // Some more printing magic 打印
    generalProps.add("FG");
    // Name of the game 名称
    generalProps.add("GN");
    // Black territory or area 来自地区
    generalProps.add("TB");
    // White territory or area 来自地区
    generalProps.add("TW");
    // Sets the move number to the given value, i.e. a move
    // specified in this node has exactly this move-number. This
    // can be useful for variations or printing.
    // SGF4J doesn't honour this atm 用于打印，移动数字
    generalProps.add("MN");
    // Handicap stones 让子
    generalProps.add("HA");
    // PL tells whose turn it is to play. 由哪方下
    generalProps.add("PL");
    // I think this is White Country 国家
    generalProps.add("WC");
    // I think this is Black Country 国家
    generalProps.add("BC");
    // I think this is just a game ID 编号
    generalProps.add("ID");

    // These are also available for nodes! 也适用于节点
    // time left for white剩余时间
    generalProps.add("WL");
    // time left for black剩余时间
    generalProps.add("BL");

  //尝试是否适用于节点
    // "AB": add black stones AB[point list] 添加子
    generalProps.add("AB");
    // "AW": add white stones AW[point list] 添加子
    generalProps.add("AW");
    // add empty = remove stones 删除子
  generalProps.add("AE");
  }

  private static final Set<String> nodeProps = new HashSet<String>();

  static {
    // Move for Black
    nodeProps.add("B");
    // Move for White
    nodeProps.add("W");

    // 下面是标记
    // marks given points with circle 圆
    nodeProps.add("CR");
    // marks given points with cross 十字
    nodeProps.add("MA");
    // marks given points with triangle 三角
    nodeProps.add("TR");
    // marks given points with square 正方
    nodeProps.add("SQ");
    // selected points 选择
    nodeProps.add("SL");
    // labels on points LB[fg:A][es:B][jk:C] 标签
    nodeProps.add("LB");
    // Number of white stones to play in this byo-yomi period 读秒
    nodeProps.add("OW");
    // Number of black stones to play in this byo-yomi period 读秒
    nodeProps.add("OB");
    // Comment 注释
    nodeProps.add("C");
    /*
     * Provides a name for the node. For more info have a look at
     * the C-property. 名称
     */
    nodeProps.add("N");
    /*
     * List of points - http://www.red-bean.com/sgf/proplist_ff.html
     * Label the given points with uppercase letters. Not used in FF 3 and FF 4!
     *
     * Replaced by LB which defines the letters also:
     * Example: L[fg][es][jk] -> LB[fg:A][es:B][jk:C] 列出
     */
    nodeProps.add("L");


    // "AB": add black stones AB[point list] 添加子
    generalProps.add("AB");
    // "AW": add white stones AW[point list] 添加子
    generalProps.add("AW");
    // add empty = remove stones 删除子
    generalProps.add("AE");
    // time left for white 剩余时间  也适用于节点
    nodeProps.add("WL");
    // time left for black 剩余时间  也适用于节点
    nodeProps.add("BL");
  }

  private Stack<GameNode> treeStack = new Stack<GameNode>();

  public Parser(String game) {
    originalGame = game;
  }

  public Game parse() {
    Game game = new Game(originalGame);

    // the root node
    GameNode parentNode = null;
    // replace token delimiters

    int moveNo = 1;

    for (int i = 0; i < originalGame.length(); i++) {
      char chr = originalGame.charAt(i);
      if (';' == chr && (i == 0 || originalGame.charAt(i - 1) != '\\')) {
        String nodeContents = consumeUntil(originalGame, i);
        i = i + nodeContents.length();

        GameNode node = parseToken(nodeContents, parentNode, game);
        if (node.isMove()) {
          node.setMoveNo(moveNo++);
        }

        if (parentNode == null) {
          parentNode = node;
          game.setRootNode(parentNode);
        }
        else if (!node.isEmpty()) {
          parentNode.addChild(node);
          parentNode = node;
        }
      }
      else if ('(' == chr && parentNode != null) {
        treeStack.push(parentNode);
      }
      else if (')' == chr) {
        if (treeStack.size() > 0) {
          parentNode = treeStack.pop();
          moveNo = parentNode.getMoveNo() + 1;
        }
      }
      else {
      }
    }

    return game;
  }

  private String consumeUntil(String gameStr, int i) {
    StringBuffer rtrn = new StringBuffer();
    boolean insideComment = false;
    for (int j = i + 1; j < gameStr.length(); j++) {
      char chr = gameStr.charAt(j);
      if (insideComment) {
        if (']' == chr && gameStr.charAt(j - 1) != '\\') {
          insideComment = false;
        }
        rtrn.append(chr);
      }
      else {
        if ('C' == chr && '[' == gameStr.charAt(j + 1)) {
          insideComment = true;
          rtrn.append(chr);
        }
        else if (';' != chr && ')' != chr && '(' != chr) {
          rtrn.append(chr);
        }
        else {
          break;
        }
      }
    }
    return rtrn.toString().trim();
  }

  private GameNode parseToken(String token, final GameNode parentNode, Game game) {
    GameNode rtrnNode = new GameNode(parentNode);
    // replace delimiters
    token = Parser.prepareToken("'" + token + "'");

    // lets find all the properties
    Pattern p = Pattern.compile("([a-zA-Z]{1,})((\\[[^\\]]*\\]){1,})");
    Matcher m = p.matcher(token);
    while (m.find()) {
      String group = m.group();
      if (group.length() == 0)
        continue;

      String key = m.group(1);
      String value = m.group(2);
      if (value.startsWith("[")) {
        value = value.substring(1, value.length() - 1);
      }

      value = Parser.normaliseToken(value);

      // these properties require some cleanup
      if ("AB".equals(key) || "AW".equals(key)) {
        // these come in as a list of coordinates while the first [ is cut off
        // and also the last ], easy to split by ][
        String[] list = value.split("\\]\\[");
        // if the parent node is null then these are
        // game properties, if not null then the node properties
        if (parentNode == null) {
          game.addProperty(key, String.join(",", list));
        }
        else {
          rtrnNode.addProperty(key, String.join(",", list));
        }
      }
      else if ("C".equals(key) || "N".equals(key)) {
        // nodes and the game can have a comment or name
        // if parent is null it is a game property
        if (parentNode == null) {
          game.addProperty(key, value);
        }
        else {
          rtrnNode.addProperty(key, value);
        }
      }
      else if (generalProps.contains(key)) {
        game.addProperty(key, value);
      }
      else if (nodeProps.contains(key)) {
        rtrnNode.addProperty(key, cleanValue(value));
      }
      else if ("L".equals(key)) {
        log.debug("Not handling " + key + " = " + value);
      }
      else {
        // log.error("Not able to parse property '" + m.group(1) + "'=" + m.group(2) + ". Found it from " + m.group(0));
        throw new SgfParseException("Ignoring property '" + m.group(1) + "'=" + m.group(2) + " Found it from '" + m.group(0) + "'");
      }
    }

    return rtrnNode;
  }

  private String cleanValue(String value) {
    String cleaned = value.replaceAll("\\\\;", ";");
    return cleaned;
  }

  private static String prepareToken(String token) {
    token = token.replaceAll("\\\\\\[", "@@@@@");
    token = token.replaceAll("\\\\\\]", "#####");
    return token;
  }

  public static String normaliseToken(String token) {
    token = token.replaceAll("@@@@@", "\\\\\\[");
    token = token.replaceAll("#####", "\\\\\\]");
    return token;
  }
}
