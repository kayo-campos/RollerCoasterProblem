import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class OnlyNumericDocumentFilter extends DocumentFilter {

	private int maxValue;
	public OnlyNumericDocumentFilter(int maxValue){
		this.maxValue = maxValue;
	}
	
	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		super.insertString(fb, offset, string, attr);
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		super.remove(fb, offset, length);
	}
	
	

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		try {
			int value = Integer.parseInt(text, 10);
			super.replace(fb, offset, length,text, attrs);
		} catch(NumberFormatException nFormat) {
			super.replace(fb, 0, offset,"", attrs);
		}	
	}
	
	
	private void testIfBigger(int value) throws NumberFormatException{
		if(value > maxValue){
			throw new NumberFormatException();
		}
	}
}
