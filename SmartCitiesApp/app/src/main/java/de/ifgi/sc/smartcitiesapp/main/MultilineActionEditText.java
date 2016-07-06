package de.ifgi.sc.smartcitiesapp.main;

/**
 * Created by Maurin on 04.07.2016.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * An EditText that lets you use actions ("Done", "Go", etc.) on multi-line edits.
 * Since a combination of those actions and multi-lining is not supported by the default EditText,
 * it was very much necessary to create our own "MultilineActionEditText" !!!1 :-)
  */
public class MultilineActionEditText extends EditText
{
    public MultilineActionEditText(Context context)
    {
        super(context);
    }

    public MultilineActionEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MultilineActionEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }
}