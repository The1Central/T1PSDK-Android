package th.co.the1.t1p.demo.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import th.co.the1.t1p.demo.R

class AlertDialogFragment : DialogFragment() {

    private lateinit var listener: NoticeDialogListener

    private var message: String = ""

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    private lateinit var buttonPositive: AppCompatButton
    private lateinit var messageTextView: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        message = arguments?.getString(MESSAGE_EXTRA) ?: ""
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_display, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageTextView = view.findViewById(R.id.message)
        messageTextView.text = message

        buttonPositive = view.findViewById(R.id.buttonPositive)
        buttonPositive.setOnClickListener {
            listener.onDialogPositiveClick(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException()
        }
    }

    companion object {
        private const val MESSAGE_EXTRA = "MESSAGE_EXTRA"

        @JvmStatic
        fun newInstance(message: String): AlertDialogFragment {
            val fragment = AlertDialogFragment()
            val bundle = Bundle()
            bundle.putString(MESSAGE_EXTRA, message)
            fragment.arguments = bundle
            return fragment
        }
    }
}
