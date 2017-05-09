import java.util.*;

public class Codegen {
  private TreeNode root;
  private String basic;
  private boolean getProcs;
  public LinkedList<Integer> procIDs = new LinkedList<Integer>();
  LinkedList<TreeNode> procCode = new LinkedList<TreeNode>();

  public Codegen (TreeNode node)  {
      getProcs = true;

     this.root = node;
     basic = "";
     generateCode();
     basic = "";
     getProcs = false;
  }
  //TODO: functions:

  public void generateCode()  {
    genCode(root);
  }

  public String toString() {
    return basic;
  }


  private void genCode(TreeNode currentNode) {
    switch(currentNode.tokenClass) {
      case "Q": //DONE
        if (currentNode.childrenSize() != 0) {
          genCode(currentNode.getChild(0));

          basic += "\nend";
        }
        break;
      case "P"://DONE
        if (currentNode.childrenSize() == 1) {
          genCode(currentNode.getChild(0));
        } else if (currentNode.childrenSize() == 2) {
          genCode(currentNode.getChild(0));
          genCode(currentNode.getChild(1));
        }
        break;
      case "D"://DONE
        if (currentNode.childrenSize() == 1)
          genCode(currentNode.getChild(0));
        else if (currentNode.childrenSize() == 2) {
          genCode(currentNode.getChild(0));
          genCode(currentNode.getChild(1));
        }
        break;
      case "R"://TODO: make proc defs work properly
        if (getProcs) {
          procIDs.add(currentNode.getChild(1).getID());
          procCode.add(currentNode.getChild(2));

          genCode(currentNode.getChild(2));
        }
        break;

      case "C":
      case "I"://DONE
        for (int x = 0; x < currentNode.childrenSize(); ++x) {
            genCode(currentNode.getChild(x));

        }
        basic += "\n";
        break;
      case "O": //DONE
        if (currentNode.getChild(0).snippet.equals("input")) {
          basic += "input ";

          genCode(currentNode.getChild(1));
        } else if (currentNode.getChild(0).snippet.equals("output")) {
          basic += "print " ;
          genCode(currentNode.getChild(1));
        }

        break;
      case "Y": //TODO: make proc defs work properly
        if (getProcs) {
          basic += "GOTO " + currentNode.getChild(0).type + currentNode.getChild(0).getID();
        } else {
          //System.out.println("Getting id " + currentNode + " \nIN " + procIDs);
          genCode(procCode.get(procIDs.indexOf(currentNode.getChild(0).getID())));
        }

        break;
      case "V": //DONE
        genCode(currentNode.getChild(0));
        break;
      case "S": //DONE
        basic += "" + currentNode.getChild(0).type + currentNode.getChild(0).getID();
        if (currentNode.getChild(0).type == 's') {
          basic += "$";
        }
        break;
      case "N"://DONE
        basic += "" + currentNode.getChild(0).type + currentNode.getChild(0).getID();
        break;
      case "A"://DONE
        genCode(currentNode.getChild(0));
        basic += currentNode.getChild(1).snippet ;
        genCode(currentNode.getChild(2));

        break;
      case "U"://DONE
        genCode(currentNode.getChild(0));
        break;
      case "T"://DONE
        genCode(currentNode.getChild(0));
        break;
      case "X": //DONE
        genCode(currentNode.getChild(0));
        break;
      case "L": //DONE

        genCode(currentNode.getChild(1));
        if (currentNode.getChild(0).snippet.equals("add")) {
          basic += "+";
        } else if (currentNode.getChild(0).snippet.equals("sub")) {
          basic += "-";
        } else if (currentNode.getChild(0).snippet.equals("mult")) {
          basic += "*";
        }

        genCode(currentNode.getChild(2));
        break;
      case "W": //DONE
        basic +=  "if (";
        genCode(currentNode.getChild(1));
        basic += ") then\n ";
        genCode(currentNode.getChild(3));

        if (currentNode.childrenSize() == 6) {
          basic += " else\n ";
          genCode(currentNode.getChild(5));
        }
        basic += "\nend if";
        break;
      case "B": //DONE
        if (currentNode.getChild(0).snippet.equals("eq") || currentNode.getChild(0).snippet.equals("and") || currentNode.getChild(0).snippet.equals("or")) {
          genCode(currentNode.getChild(1));
          if (currentNode.getChild(0).snippet.equals("eq")) {
            basic += " = ";
          } else  if (currentNode.getChild(0).snippet.equals("and")) {
            basic += " AND ";
          } else {
            basic +=  " OR ";
          }
          genCode(currentNode.getChild(2));
        } else if ((currentNode.getChild(0).snippet.equals("not"))) {
          basic += currentNode.getChild(0).snippet + "(";
          genCode(currentNode.getChild(1));
          basic += ")";
        } else {
          basic += "(" + currentNode.getChild(0).getChild(0).type + currentNode.getChild(0).getChild(0).getID() + currentNode.getChild(1).snippet + currentNode.getChild(2).getChild(0).type +  currentNode.getChild(2).getChild(0).getID() + ")";
        }
        break;
      case "Z": //DONE
        if (currentNode.getChild(0).snippet.equals("while")) {
          basic += "while ";
          genCode(currentNode.getChild(1));
          basic += "\n";
          genCode(currentNode.getChild(2));
          basic += "\nwend";
        } else {
          basic += "for ";
          basic += "" + currentNode.getChild(1).getChild(0).type + currentNode.getChild(1).getChild(0).getID() + " = " + currentNode.getChild(3).snippet + " TO " + "(" + currentNode.getChild(6).getChild(0).type + currentNode.getChild(6).getChild(0).getID() + "-1)\n";
          genCode(currentNode.getChild(12));
          basic += "\nnext " + currentNode.getChild(1).getChild(0).type + currentNode.getChild(1).getChild(0).getID();
        }
        break;
      case "keyword": //DONE
        basic += "end";
        break;
      case "short string": //DONE
        basic += currentNode.snippet;
        break;
      case "integer": //DONE
        basic += currentNode.snippet;
        break;
      default:
        System.out.println("Unknown error!");
    }

  }
}
