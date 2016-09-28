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
 *数据类型
 * move m
 * setup t
 * root r
 * game info gi
 *
 * none o
 *
 * number n
 * double d
 * real r
 *
 * stone s
 * point p
 * color c
 * text x
 * simpletext sx
 *
 * list <
 *
 * point:point p:p
 * point:simpletext p:sx
 *
 move走棋动作	    B走黑 m m, KO强制走棋甚至违背规则 m o, MN设定手数用于打印 m n, W走白 m m

 setup设置    	AB添加黑 t <s, AE删除子 t <p, AW添加白 t <s, PL设置下棋方 t c

 -标记信息	    AR箭头 - p:p, CR圆 - <p, DD灰度 - <p, LB标签 - p:sx, LN线 - p:p, MA标记X - <p, SL选择 - <p, SQ方块 - <p, TR三角 - <p

 -节点信息	    C备注 - x, DM两分 - d, GB黑优 - d, GW白优 - d, HO热点 - d, N名称 - sx, UC形势不明 - d, V价值 - r

 move走棋信息	    BM恶手 m d, DO疑问手 m o, IT有趣 m n, TE手筋 m d

 root根结点属性	AP应用 r sx:sx, CA编码 r sx, FF版本 r n, GM游戏种类 r n, ST显示信息方式 r n, SZ横盘大小 r n

 GI棋局信息	    AN讲解人 gi sx, BR黑段位 gi sx, BT黑队 gi sx, CP版权 gi sx, DT时间 gi sx, EV赛事 gi sx,
                GN棋谱名 gi sx, GC扩展信息 gi x, ON发起 gi sx, OT用时 gi sx, PB黑方姓名 gi sx, PC地点 gi sx, PW白言姓名 gi sx,
                RE结果 gi sx (B+2.5), RO手数 gi sx, RU规则 gi sx, SO来源 gi sx, TM限时 gi r,
                US记录员 gi sx, WR白段位 gi sx, WT白队 gi sx, KM贴目 gi r, HA让子 gi n

 move时间信息	    BL黑剩时 m r, OB黑手数 m n, OW白手数 m n, WL白剩时 m r
 *

 结构：root  GameTree  Sequence[Node | GameTree]{...}

 // http://www.red-bean.com/sgf/properties.html


 Basic (EBNF) Definition
 The conventions of EBNF are discussed in literature, and on WWW in the Computing Dictionary.
 A quick summary:


 "..." : terminal symbols
 [...] : option: occurs at most once
 {...} : repetition: any number of times, including zero
 (...) : grouping
 |   : exclusive or
 italics: parameter explained at some other place



 EBNF Definition

 Collection = GameTree { GameTree }
 GameTree   = "(" Sequence { GameTree } ")"
 Sequence   = Node { Node }
 Node       = ";" { Property }
 Property   = PropIdent PropValue { PropValue }
 PropIdent  = UcLetter { UcLetter }
 PropValue  = "[" CValueType "]"
 CValueType = (ValueType | Compose)
 ValueType  = (None | Number | Real | Double | Color | SimpleText |
 Text | Point  | Move | Stone)


 Property Value Types


 UcLetter   = "A".."Z"
 Digit      = "0".."9"
 None       = ""

 Number     = [("+"|"-")] Digit { Digit }
 Real       = Number ["." Digit { Digit }]

 Double     = ("1" | "2")
 Color      = ("B" | "W")

 SimpleText = { any character (handling see below) }
 Text       = { any character (handling see below) }

 Point      = game-specific
 Move       = game-specific
 Stone      = game-specific

 Compose    = ValueType ":" ValueType


 Property Types (currently five):

 move
 setup
 root
 game-info

 inherit
 (;GM[1]SZ[9]FF[4]
 AB[ac:ic]AW[ae:ie]
 ;DD[aa:bi][ca:ce]
 VW[aa:bi][ca:ee][ge:ie][ga:ia][gc:ic][gb][ib]
 )



 Property index of FF[1]-FF[4]
 This is an alphabetical index to all properties defined in FF[1] (as in Kierulf's thesis), FF[3] (as on Martin's pages) and FF[4] (as in this spec).
 Note: FF[2] was never made public. It's more or less identical to FF[1] - the only changes known (to me) are that the BS/WS values had been redefined.

 ID   FF   Description     property type    property value
 --  ----  --------------  ---------------  ---------------------------------
 AB  1234  Add Black       setup            list of stone
 AE  1234  Add Empty       setup            list of point
 AN  --34  Annotation      game-info        simpletext
 AP  ---4  Application     root             composed simpletext : simpletext
 AR  ---4  Arrow           -                list of composed point : point
 AS  ---4  Who adds stones - (LOA)          simpletext
 AW  1234  Add White       setup            list of stone
 B   1234  Black           move             move
 BL  1234  Black time left move             real
 BM  1234  Bad move        move             double
 BR  1234  Black rank      game-info        simpletext
 BS  123-  Black species   game-info        number
 BT  --34  Black team      game-info        simpletext
 C   1234  Comment         -                text
 CA  ---4  Charset         root             simpletext
 CH  123-  Check mark      -                double
 CP  --34  Copyright       game-info        simpletext
 CR  --34  Circle          -                list of point
 DD  ---4  Dim points      - (inherit)      elist of point
 DM  --34  Even position   -                double
 DO  --34  Doubtful        move             none
 DT  1234  Date            game-info        simpletext
 EL  12--  Eval. comp move -                number
 EV  1234  Event           game-info        simpletext
 EX  12--  Expected move   -                move
 FF  -234  Fileformat      root             number (range: 1-4)
 FG  1234  Figure          -                none | composed number : simpletext
 GB  1234  Good for Black  -                double
 GC  1234  Game comment    game-info        text
 GM  1234  Game            root             number (range: 1-5,7-16)
 GN  1234  Game name       game-info        simpletext
 GW  1234  Good for White  -                double
 HA  1234  Handicap        game-info (Go)   number
 HO  --34  Hotspot         -                double
 ID  --3-  Game identifier game-info        simpletext
 IP  ---4  Initial pos.    game-info (LOA)  simpletext
 IT  --34  Interesting     move             none
 IY  ---4  Invert Y-axis   game-info (LOA)  simpletext
 KM  1234  Komi            game-info (Go)   real
 KO  --34  Ko              move             none
 L   12--  Letters         -                list of point
 LB  --34  Label           -                list of composed point : simpletext
 LN  --34  Line            -                list of composed point : point
 LT  --3-  Lose on time    -                none
 M   12--  Simple markup   -                list of point
 MA  --34  Mark with X     -                list of point
 MN  --34  Set move number move             number
 N   1234  Nodename        -                simpletext
 OB  --34  OtStones Black  move             number
 OM  --3-  Moves/overtime  -                number
 ON  --34  Opening         game-info        simpletext
 OP  --3-  Overtime length -                real
 OT  ---4  Overtime        game-info        simpletext
 OV  --3-  Operator overhead -              real
 OW  --34  OtStones White  move             number
 PB  1234  Player Black    game-info        simpletext
 PC  1234  Place           game-info        simpletext
 PL  1234  Player to play  setup            color
 PM  ---4  Print move mode - (inherit)      number
 PW  1234  Player White    game-info        simpletext
 RE  1234  Result          game-info        simpletext
 RG  123-  Region          - (Go)           list of point
 RO  1234  Round           game-info        simpletext
 RU  --34  Rules           game-info        simpletext
 SC  123-  Secure stones   -                list of point
 SE  --3-  Selftest moves  -                list of point
 SE  ---4  Markup          - (LOA)          point
 SI  --3-  Sigma           -                double
 SL  1234  Selected        -                list of point
 SO  1234  Source          game-info        simpletext
 SQ  ---4  Square          -                list of point
 ST  ---4  Style           root             number (range: 0-3)
 SU  ---4  Setup type      game-info (LOA)  simpletext
 SZ  1234  Size            root             (number | composed number : number)
 TB  1234  Territory Black - (Go)           elist of point
 TC  --3-  Territory count - (Go)           number
 TE  1234  Tesuji          move             double
 TM  1234  Timelimit       game-info        real
 TR  --34  Triangle        -                list of point
 TW  1234  Territory White - (Go)           elist of point
 UC  --34  Unclear pos     -                double
 US  1234  User            game-info        simpletext
 V   1234  Value (score)   -                real
 VW  1234  View            - (inherit)      elist of point
 W   1234  White           move             move
 WL  1234  White time left move             real
 WR  1234  White rank      game-info        simpletext
 WS  123-  White species   game-info        number
 WT  --34  White team      game-info        simpletext

 */