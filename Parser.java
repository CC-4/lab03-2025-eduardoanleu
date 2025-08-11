/*
    Laboratorio No. 3 - Recursive Descent Parsing
    CC4 - Compiladores

    Clase que representa el parser

    Actualizado: agosto de 2021, Luis Cu
*/

import java.util.LinkedList;
import java.util.Stack;

public class Parser {

    private int next;
    private Stack<Double> operandos;
    private Stack<Token> operadores;
    private LinkedList<Token> tokens;

    public boolean parse(LinkedList<Token> tokens) {
        this.tokens = tokens;
        this.next = 0;
        this.operandos = new Stack<Double>();
        this.operadores = new Stack<Token>();

        System.out.println("Aceptada? " + S());

        if (!this.operandos.empty()) {
            System.out.println("Resultado: " + this.operandos.peek());
        }

        if(this.next != this.tokens.size()) {
            return false;
        }
        return true;
    }

    private boolean term(int id) {
        if(this.next < this.tokens.size() && this.tokens.get(this.next).equals(id)) {
            
            if (id == Token.NUMBER) {
                operandos.push( this.tokens.get(this.next).getVal() );

            } else if (id == Token.SEMI) {
                while (!this.operadores.empty()) {
                    popOp();
                }
                
            } else {
                pushOp( this.tokens.get(this.next) );
            }

            this.next++;
            return true;
        }
        return false;
    }

    private int pre(Token op) {
        switch(op.getId()) {
            case Token.PLUS:
            case Token.MINUS:
                return 1;
            case Token.MULT:
            case Token.DIV:
            case Token.MOD:
                return 2;
            case Token.EXP:
                return 3;
            case Token.UNARY:
                return 4;
            case Token.LPAREN:
                return 0;
            default:
                return -1;
        }
    }

    private void popOp() {
        Token op = this.operadores.pop();

        if (op.equals(Token.PLUS)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(a + b);
        } else if (op.equals(Token.MINUS)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(a - b);
        } else if (op.equals(Token.MULT)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(a * b);
        } else if (op.equals(Token.DIV)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(a / b);
        } else if (op.equals(Token.MOD)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(a % b);
        } else if (op.equals(Token.EXP)) {
            double b = this.operandos.pop();
            double a = this.operandos.pop();
            this.operandos.push(Math.pow(a, b));
        } else if (op.equals(Token.UNARY)) {
            double a = this.operandos.pop();
            this.operandos.push(-a);
        }
    }

    private void pushOp(Token op) {
        if (op.equals(Token.LPAREN)) {
            this.operadores.push(op);
            return;
        }

        if (op.equals(Token.RPAREN)) {
            while (!this.operadores.empty() && !this.operadores.peek().equals(Token.LPAREN)) {
                popOp();
            }
            if (!this.operadores.empty()) {
                this.operadores.pop();
            }
            return;
        }

        while (!this.operadores.empty() && 
               pre(this.operadores.peek()) >= pre(op) && 
               !this.operadores.peek().equals(Token.LPAREN)) {
            
            if (op.equals(Token.EXP) && this.operadores.peek().equals(Token.EXP)) {
                break;
            }
            popOp();
        }
        
        this.operadores.push(op);
    }

    private boolean S() {
        return E() && term(Token.SEMI);
    }

    private boolean E() {
        return T() && Ep();
    }

    private boolean Ep() {
        if (this.next < this.tokens.size()) {
            if (this.tokens.get(this.next).equals(Token.PLUS)) {
                return term(Token.PLUS) && T() && Ep();
            } else if (this.tokens.get(this.next).equals(Token.MINUS)) {
                return term(Token.MINUS) && T() && Ep();
            }
        }
        return true;
    }

    private boolean T() {
        return F() && Tp();
    }

    private boolean Tp() {
        if (this.next < this.tokens.size()) {
            if (this.tokens.get(this.next).equals(Token.MULT)) {
                return term(Token.MULT) && F() && Tp();
            } else if (this.tokens.get(this.next).equals(Token.DIV)) {
                return term(Token.DIV) && F() && Tp();
            } else if (this.tokens.get(this.next).equals(Token.MOD)) {
                return term(Token.MOD) && F() && Tp();
            }
        }
        return true;
    }

    private boolean F() {
        return U() && Fp();
    }

    private boolean Fp() {
        if (this.next < this.tokens.size() && this.tokens.get(this.next).equals(Token.EXP)) {
            return term(Token.EXP) && U() && Fp();
        }
        return true;
    }

    private boolean U() {
        if (this.next < this.tokens.size() && this.tokens.get(this.next).equals(Token.UNARY)) {
            return term(Token.UNARY) && U();
        } else {
            return P();
        }
    }

    private boolean P() {
        if (this.next < this.tokens.size()) {
            if (this.tokens.get(this.next).equals(Token.LPAREN)) {
                return term(Token.LPAREN) && E() && term(Token.RPAREN);
            } else if (this.tokens.get(this.next).equals(Token.NUMBER)) {
                return term(Token.NUMBER);
            }
        }
        return false;
    }
}