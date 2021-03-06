package de.tjarksaul.wachmanager.modules.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import de.tjarksaul.wachmanager.R
import de.tjarksaul.wachmanager.dtos.Station
import de.tjarksaul.wachmanager.modules.base.BaseFragment
import de.tjarksaul.wachmanager.modules.main.MainActivity
import de.tjarksaul.wachmanager.util.TextChangeListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_splash.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class SplashFragment : BaseFragment() {
    private val disposable = CompositeDisposable()

    private val actions: PublishSubject<SplashViewAction> = PublishSubject.create()

    private val splashViewModel: SplashViewModel by viewModel()

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        setupBindings()
    }

    override fun onResume() {
        super.onResume()

        actions.onNext(SplashViewAction.Refetch)
    }

    private fun setupView() {
        editCrewName.requestFocus()
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = requireActivity().currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setupBindings() {
        splashViewModel.attach(actions)

        disposable += splashViewModel.stateOf { stations }
            .subscribe {
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    it
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerStationName.adapter = adapter
                }
            }

        disposable += splashViewModel.stateOf { selectedStation }
            .subscribe { index ->
                spinnerStationName?.setSelection(index)
            }

        disposable += splashViewModel.stateOf { currentDate }
            .subscribe {
                dateTextView.text = it
            }

        spinnerStationName.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                actions.onNext(SplashViewAction.SelectStation(position))
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                actions.onNext(SplashViewAction.SelectStation(0))
            }
        }

        editCrewName.addTextChangedListener(object : TextChangeListener() {
            override fun onTextChanged(s: Editable?) {
                s?.toString()?.let {
                    actions.onNext(SplashViewAction.UpdateCrewName(it))
                }
            }
        })

        startButton.setOnClickListener {
            actions.onNext(SplashViewAction.Submit)
        }

        disposable += splashViewModel.effect<SplashViewEffect.Dismiss>()
            .subscribe { dismiss() }
    }

    private fun dismiss() {
        hideKeyboard()

        val activity: MainActivity = activity as MainActivity

        activity.goToStationView()
    }

    private fun setStationAndCrewNames() {
        val crewNames = editCrewName.text.toString()
        val station = spinnerStationName.selectedItem as Station
        val stationName = station.name
        val stationId = station.id

        saveVal("stationId", stationId)
        saveVal("stationName", stationName)
        saveVal("crewNames", crewNames)

        saveIntVal("lastStationSelectedDate", Date().time)
    }

}