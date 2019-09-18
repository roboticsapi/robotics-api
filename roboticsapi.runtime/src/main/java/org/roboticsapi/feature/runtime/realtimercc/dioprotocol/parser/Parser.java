package org.roboticsapi.feature.runtime.realtimercc.dioprotocol.parser;

import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOCommand;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOFloat;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOInteger;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOLiteral;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOParameter;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOParameterList;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOParameterMap;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOStatement;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOString;

public class Parser {
	public static final int _EOF = 0;
	public static final int _string = 1;
	public static final int _identifier = 2;
	public static final int _integer = 3;
	public static final int _float = 4;
	public static final int maxT = 14;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t; // last recognized token
	public Token la; // lookahead token
	int errDist = minErrDist;

	public Scanner scanner;
	public Errors errors;

	public DIOStatement diostatement;

	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr(int n) {
		if (errDist >= minErrDist)
			errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr(String msg) {
		if (errDist >= minErrDist)
			errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}

	void Get() {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}

	void Expect(int n) {
		if (la.kind == n)
			Get();
		else {
			SynErr(n);
		}
	}

	boolean StartOf(int s) {
		return set[s][la.kind];
	}

	void ExpectWeak(int n, int follow) {
		if (la.kind == n)
			Get();
		else {
			SynErr(n);
			while (!StartOf(follow))
				Get();
		}
	}

	boolean WeakSeparator(int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) {
			Get();
			return true;
		} else if (StartOf(repFol))
			return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}

	DIOLiteral DIOStringP() {
		DIOLiteral lit;
		Expect(1);
		lit = new DIOString(t.val, true);
		return lit;
	}

	DIOLiteral DIOIntegerP() {
		DIOLiteral lit;
		Expect(3);
		lit = new DIOInteger(t.val);
		return lit;
	}

	DIOLiteral DIOFloatP() {
		DIOLiteral lit;
		Expect(4);
		lit = new DIOFloat(t.val);
		return lit;
	}

	DIOLiteral DIOLiteralP() {
		DIOLiteral lit;
		lit = null;
		if (la.kind == 1) {
			lit = DIOStringP();
		} else if (la.kind == 3) {
			lit = DIOIntegerP();
		} else if (la.kind == 4) {
			lit = DIOFloatP();
		} else
			SynErr(15);
		return lit;
	}

	void DIOMapP(DIOParameterMap pmap) {
		String key = "";
		if (la.kind == 2) {
			Get();
			key = t.val;
		} else if (la.kind == 1) {
			Get();
			key = DIOString.unescape(t.val);
		} else
			SynErr(16);
		Expect(5);
		DIOParameter param = DIOParameterP();
		pmap.addParameter(key, param);
	}

	DIOParameter DIOParameterP() {
		DIOParameter param;
		param = null;
		if (la.kind == 1 || la.kind == 3 || la.kind == 4) {
			DIOLiteral lit = DIOLiteralP();
			param = lit;
		} else if (la.kind == 6) {
			Get();
			DIOParameterList paramlist = new DIOParameterList();
			param = paramlist;
			if (StartOf(1)) {
				DIOParameter nparam = DIOParameterP();
				paramlist.addParameter(nparam);
				while (la.kind == 7) {
					Get();
					DIOParameter nparam2 = DIOParameterP();
					paramlist.addParameter(nparam2);
				}
			}
			Expect(8);
		} else if (la.kind == 9) {
			Get();
			DIOParameterMap pmap = new DIOParameterMap();
			param = pmap;
			if (la.kind == 1 || la.kind == 2) {
				DIOMapP(pmap);
				while (la.kind == 7) {
					Get();
					DIOMapP(pmap);
				}
			}
			Expect(10);
		} else
			SynErr(17);
		return param;
	}

	void DIOParametersP(DIOCommand dioc) {
		if (StartOf(1)) {
			DIOParameter diop = DIOParameterP();
			dioc.addParameter(diop);
			while (la.kind == 7) {
				Get();
				DIOParameter diop2 = DIOParameterP();
				dioc.addParameter(diop2);
			}
		}
	}

	void DIOCommandP(DIOStatement dios) {
		Expect(2);
		dios.setTag(t.val);
		Expect(11);
		Expect(2);
		DIOCommand cmd = new DIOCommand(t.val);
		Expect(12);
		DIOParametersP(cmd);
		Expect(13);
		dios.setCommand(cmd);
	}

	void DIOProtocolP() {
		diostatement = new DIOStatement();
		DIOCommandP(diostatement);
	}

	public void Parse() {
		la = new Token();
		la.val = "";
		Get();
		DIOProtocolP();
		Expect(0);

	}

	private static final boolean[][] set = { { T, x, x, x, x, x, x, x, x, x, x, x, x, x, x, x },
			{ x, T, x, T, T, x, T, x, x, T, x, x, x, x, x, x }

	};
} // end Parser

class Errors {
	public int count = 0; // number of errors detected
	public java.io.PrintStream errorStream = System.out; // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text

	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) {
			b.delete(pos, pos + 3);
			b.insert(pos, line);
		}
		pos = b.indexOf("{1}");
		if (pos >= 0) {
			b.delete(pos, pos + 3);
			b.insert(pos, column);
		}
		pos = b.indexOf("{2}");
		if (pos >= 0)
			b.replace(pos, pos + 3, msg);
		errorStream.println(b.toString());
	}

	public void SynErr(int line, int col, int n) {
		String s;
		switch (n) {
		case 0:
			s = "EOF expected";
			break;
		case 1:
			s = "string expected";
			break;
		case 2:
			s = "identifier expected";
			break;
		case 3:
			s = "integer expected";
			break;
		case 4:
			s = "float expected";
			break;
		case 5:
			s = "\":\" expected";
			break;
		case 6:
			s = "\"[\" expected";
			break;
		case 7:
			s = "\",\" expected";
			break;
		case 8:
			s = "\"]\" expected";
			break;
		case 9:
			s = "\"{\" expected";
			break;
		case 10:
			s = "\"}\" expected";
			break;
		case 11:
			s = "\"=\" expected";
			break;
		case 12:
			s = "\"(\" expected";
			break;
		case 13:
			s = "\")\" expected";
			break;
		case 14:
			s = "??? expected";
			break;
		case 15:
			s = "invalid DIOLiteralP";
			break;
		case 16:
			s = "invalid DIOMapP";
			break;
		case 17:
			s = "invalid DIOParameterP";
			break;
		default:
			s = "error " + n;
			break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr(int line, int col, String s) {
		printMsg(line, col, s);
		count++;
	}

	public void SemErr(String s) {
		errorStream.println(s);
		count++;
	}

	public void Warning(int line, int col, String s) {
		printMsg(line, col, s);
	}

	public void Warning(String s) {
		errorStream.println(s);
	}
} // Errors

class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;

	public FatalError(String s) {
		super(s);
	}
}
