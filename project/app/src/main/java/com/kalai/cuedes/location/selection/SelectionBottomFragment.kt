package com.kalai.cuedes.location.selection


import android.app.Dialog
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import android.view.*
import android.view.KeyEvent.KEYCODE_BACK
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kalai.cuedes.R
import com.kalai.cuedes.databinding.FragmentSelectionBinding


class SelectionBottomFragment(private var latLng: LatLng) : BottomSheetDialogFragment(){

    companion object{
        private const val TAG = "SelectionFragment"
    }


    private lateinit var binding:FragmentSelectionBinding
    private val selectionViewModel: SelectionViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectionBinding.inflate(inflater,container,false)
        childFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.selection_fragment_container_view,LocationNameFragment(),"LocationFragment")
            addToBackStack(null)

        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
        Log.d(TAG, dialog?.window?.enterTransition?.name.toString())
        Log.d(TAG,dialog?.window?.enterTransition?.duration.toString())
        Log.d(TAG,  ((this as DialogFragment).enterTransition as Transition?)?.name.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialogFragment = super.onCreateDialog(savedInstanceState)
        bottomSheetDialogFragment.setOnShowListener {
            val bottomSheet = bottomSheetDialogFragment.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let { BottomSheetBehavior.from(bottomSheet).apply { isHideable = false
                state =  BottomSheetBehavior.STATE_EXPANDED

            } }

            bottomSheetDialogFragment.setCanceledOnTouchOutside(false)
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height =  context?.resources?.displayMetrics?.heightPixels?.div(2.5f)?.toInt() ?: WindowManager.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams
            dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND )


        }
        Log.d(TAG,dialog?.window?.enterTransition?.duration.toString())
        Log.d(TAG,  ((this as DialogFragment).enterTransition as Transition?)?.name.toString())
        childFragmentManager.addOnBackStackChangedListener {
            if(childFragmentManager.backStackEntryCount == 0){
                dialog?.dismiss()
            }
        }



        bottomSheetDialogFragment.setOnKeyListener{ dialogInterface, i, keyEvent ->
            if(keyEvent != null && keyEvent.keyCode == KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                Log.d(TAG, "onBackPressed()" + childFragmentManager.backStackEntryCount + i)
                if (childFragmentManager.fragments.size == 1) {
                    Log.d(TAG,"Going to be dismissed")
                    val result = bundleOf("Successful" to false)
                    /*TODO need to make req key const*/
                    parentFragment?.parentFragmentManager?.setFragmentResult("LocationFragmentReqKey",result)
                    dialogInterface.dismiss()
                } else {
                    childFragmentManager.popBackStack()
                    childFragmentManager.commit {
                        setTransition(TRANSIT_FRAGMENT_FADE)
                        show(childFragmentManager.fragments.last())
                    }
                }
            }

            true
        }
        return bottomSheetDialogFragment
    }

    override fun getTheme(): Int = R.style.ThemeOverlay_BottomSheetDialog

}