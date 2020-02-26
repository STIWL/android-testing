package com.luisansal.jetpack.ui.features.manageusers.newuser

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer

import com.luisansal.jetpack.R
import com.luisansal.jetpack.common.interfaces.ActionsViewPagerListener
import com.luisansal.jetpack.common.interfaces.CrudListener
import com.luisansal.jetpack.domain.entity.User
import com.luisansal.jetpack.domain.analytics.TagAnalytics
import com.luisansal.jetpack.ui.features.analytics.FirebaseAnalyticsPresenter
import com.luisansal.jetpack.ui.features.manageusers.RoomViewModel
import com.luisansal.jetpack.ui.utils.injectFragment
import kotlinx.android.synthetic.main.fragment_new_user.*
import org.koin.android.ext.android.inject
import java.lang.StringBuilder

class NewUserFragment : Fragment(), NewUserMVP.View {

    private val newUserPresenter: NewUserPresenter by injectFragment()

    private val firebaseAnalyticsPresenter: FirebaseAnalyticsPresenter by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = mViewModel.user
        if (user != null) {
            etDni.setText(user.dni)
            printUser(user)
        }
        onClickBtnSiguiente()
        onClickBtnListado()
        onTextDniChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActionsViewPagerListener) {
            mActivityListener = context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement " + mActivityListener!!.javaClass.getSimpleName())
        }
    }

    override fun onDetach() {
        super.onDetach()
        mActivityListener = null
    }

    override fun printUser(user: User) {
        mViewModel.user = user
        etNombre?.setText(user.name)
        etApellido?.setText(user.lastName)
        tvResultado?.text = StringBuilder().append(user.name).append(user.lastName)
    }

    override fun onClickBtnSiguiente() {
        btnSiguiente?.setOnClickListener {
            val user = User()
            user.name = etNombre!!.text.toString()
            user.lastName = etApellido!!.text.toString()
            user.dni = etDni!!.text.toString()
            tvResultado!!.text = StringBuilder().append(user.name).append(user.lastName)

            newUserPresenter.newUser(user)

            firebaseAnalyticsPresenter.enviarEvento(TagAnalytics.EVENTO_CREAR_USUARIO)
            mActivityListener!!.onNext()
        }
    }

    override fun notifySavedUser(name: String) {
        Toast.makeText(context, name, Toast.LENGTH_LONG).show()
    }

    override fun onTextDniChanged() {
        etDni?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                newUserPresenter.getUser(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    override fun onClickBtnListado() {
        btnListado?.setOnClickListener { mCrudListener!!.onList() }
    }

    companion object {

        var TAG = NewUserFragment::class.java.getName()

        private var mActivityListener: ActionsViewPagerListener? = null
        private var mCrudListener: CrudListener<User>? = null
        private lateinit var mViewModel: RoomViewModel


        // TODO: Rename and change types and number of parameters
        fun newInstance(activityListener: ActionsViewPagerListener?, crudListener: CrudListener<User>, viewModel: RoomViewModel): NewUserFragment {
            val fragment = NewUserFragment()
            val args = Bundle()
            fragment.arguments = args
            mActivityListener = activityListener
            mCrudListener = crudListener
            mViewModel = viewModel
            return fragment
        }
    }
}
