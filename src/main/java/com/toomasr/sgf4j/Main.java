package com.toomasr.sgf4j;

import com.toomasr.sgf4j.parser.Game;
import com.toomasr.sgf4j.parser.GameNode;

import java.nio.file.Paths;

/**
 * Created by fsp on 16-9-24.
 */
public class Main {
    public static void main(String[] args){
        Game game = Sgf.createFromPath(Paths.get("TM.sgf"));

        for(String key : game.getProperties().keySet()){
            System.out.println(key + ":" + game.getProperty(key));
        }

        GameNode node = game.getRootNode();
        do {
            System.out.println(node);
        }
        while ((node = node.getNextNode()) != null);
        System.out.print("end");
    }
}


/**
 *
 Contents
 走棋动作	B走黑, KO强制走棋甚至违背规则, MN设定手数用于打印, W走白

 设置	AB添加黑, AE删除子, AW添加白, PL设置下棋方

 标记信息	AR箭头, CR圆, DD灰度, LB标签, LN线, MA标记X, SL选择, SQ方块, TR三角

 节点信息	C备注, DM问题手, GB黑优, GW白优, HO热点, N名称, UC状况不清晰, V价值
 走棋信息	BM坏棋, DO不明, IT有趣, TE手筋


 根结点属性	AP应用, CA编码, FF版本, GM游戏种类, ST显示信息方式, SZ横盘大小
 棋局信息	AN讲解人, BR黑段们, BT黑队, CP版权, DT时间, EV赛事, GN游戏名, GC扩展信息, ON发起, OT用时, PB黑方姓名, PC地点, PW白言姓名,
            RE结果, RO手数, RU规则, SO来源, TM限时, US记录员, WR白段位, WT白队
 时间信息	BL黑剩时, OB黑手数, OW白手数, WL白剩时
 其它	FG打印用, PM打印方式, VW仅可见列表
 *
 * 以下为 MultiGo 支持的 SGF 属性：

 Move Properties
 B走黑, W走白, MN设定手数

 Setup Properties
 AB添加黑子, AW添加白子, AE删除棋子

 Root Properties
 SZ棋盘大小

 Game Information Properties
 GN棋谱名, GC备注, EV赛事, RO回合, DT时间, PC地点, PB黑方, BR黑段位, BT黑代表队, PW白方, WR白段位,
 WT白代表队, KM贴目, HA让子, TM时限, RE结果, US编者, SO来源, CP版权,
 AN注解,
 CA字符集, FF文件格式, GM对局类别1围棋, AP应用软件

 Other Properties
 C评论, N节点名称, CR圆形, TR三角, SQ方块, MA标记X, LB标签, TB黑棋势力范围, TW白棋势力范围
 TE手筋或妙手， BM恶手, DO疑问手, IT有趣手, GB黑好, GW白好, DM两分, UC形势不明
 PL轮某方走


 // http://www.red-bean.com/sgf/properties.html
 private static final Set<String> generalProps = new HashSet<String>();

 static {
 // Application used to generate the SGF
 generalProps.add("AP");
 // Black's Rating
 generalProps.add("BR");
 // White's Rating
 generalProps.add("WR");
 // KOMI
 generalProps.add("KM");
 // Black Player Extended information
 generalProps.add("PBX");
 // Black Player name
 generalProps.add("PB");
 // White Player name
 generalProps.add("PW");
 // I think - Black Player name
 generalProps.add("PX");
 // I think - White Player name
 generalProps.add("PY");
 // Charset
 generalProps.add("CA");
 // File format
 generalProps.add("FF");
 // Game type - 1 means Go
 generalProps.add("GM");
 // Size of the board
 generalProps.add("SZ");
 // Annotator
 generalProps.add("AN");
 // Name of the event
 generalProps.add("EV");
 // Name of the event extended
 // Extended info about the event
 generalProps.add("EVX");
 // Rount number
 generalProps.add("RO");
 // Rules
 generalProps.add("RU");
 // Time limit in seconds
 generalProps.add("TM");
 // How overtime is handled
 generalProps.add("OT");
 // Date of the game
 generalProps.add("DT");
 // Extended date
 generalProps.add("DTX");
 // Place of the game
 generalProps.add("PC");
 // Result of the game
 generalProps.add("RE");
 // I think - Result of the game
 generalProps.add("ER");
 // How to show comments
 generalProps.add("ST");

 *
 * Provides some extra information about the following game.
 * The intend of GC is to provide some background information
 * and/or to summarize the game itself.
 *
generalProps.add("GC");
        // Any copyright information
        generalProps.add("CP");
        // Provides name of the source
        generalProps.add("SO");
        // Name of the white team
        generalProps.add("WT");
        // Name of the black team
        generalProps.add("BT");
        // name of the user or program who entered the game
        generalProps.add("US");
        // How to print move numbers
        generalProps.add("PM");
        // Some more printing magic
        generalProps.add("FG");
        // Name of the game
        generalProps.add("GN");
        // Black territory or area
        generalProps.add("TB");
        // White territory or area
        generalProps.add("TW");
        // Sets the move number to the given value, i.e. a move
        // specified in this node has exactly this move-number. This
        // can be useful for variations or printing.
        // SGF4J doesn't honour this atm
        generalProps.add("MN");
        // Handicap stones
        generalProps.add("HA");
        // "AB": add black stones AB[point list]
        generalProps.add("AB");
        // "AW": add white stones AW[point list]
        generalProps.add("AW");
        // add empty = remove stones
        generalProps.add("AE");
        // PL tells whose turn it is to play.
        generalProps.add("PL");
        // KGSDE - kgs scoring - marks all prisoner stones
        // http://senseis.xmp.net/?CgobanProblemsAndSolutions
        generalProps.add("KGSDE");
        // KGS - score white
        generalProps.add("KGSSW");
        // KGS - score black
        generalProps.add("KGSSB");
        // Checkmark - ignored in FF4
        // http://www.red-bean.com/sgf/ff1_3/ff3.html and http://www.red-bean.com/sgf/changes.html
        generalProps.add("CH");
        // I think this is White Country
        generalProps.add("WC");
        // "LT": enforces losing on time LT[]
        // http://www.red-bean.com/sgf/ff1_3/ff3.html
        // I don't get it but I'm parsing it
        generalProps.add("LT");
        // I think this is Black Country
        generalProps.add("BC");
        // I think this is just a game ID
        generalProps.add("ID");
        // I have no idea what these properties means
        // but they are in many games of the collections
        // I've downloaded from the interwebs
        generalProps.add("OH");
        generalProps.add("LC");
        generalProps.add("RD"); // maybe release date?
        generalProps.add("TL"); // something to do with time
        generalProps.add("GK"); // something to do with the game

        // These are also available for nodes!

        // time left for white
        generalProps.add("WL");
        // time left for black
        generalProps.add("BL");
        }

private static final Set<String> nodeProps = new HashSet<String>();

static {
        // Move for Black
        nodeProps.add("B");
        // Move for White
        nodeProps.add("W");
        // marks given points with circle
        nodeProps.add("CR");
        // marks given points with cross
        nodeProps.add("MA");
        // marks given points with square
        nodeProps.add("SQ");
        // selected points
        nodeProps.add("SL");
        // labels on points
        nodeProps.add("LB");
        // marks given points with triangle
        nodeProps.add("TR");
        // Number of white stones to play in this byo-yomi period
        nodeProps.add("OW");
        // Number of black stones to play in this byo-yomi period
        nodeProps.add("OB");
        // time left for white
        nodeProps.add("WL");
        // time left for black
        nodeProps.add("BL");
        // Comment
        nodeProps.add("C");
    *
     * Provides a name for the node. For more info have a look at
     * the C-property.
     *
        nodeProps.add("N");
    *
     * List of points - http://www.red-bean.com/sgf/proplist_ff.html
     * Label the given points with uppercase letters. Not used in FF 3 and FF 4!
     *
     * Replaced by LB which defines the letters also:
     * Example: L[fg][es][jk] -> LB[fg:A][es:B][jk:C]
     *
        nodeProps.add("L");
        }

 */