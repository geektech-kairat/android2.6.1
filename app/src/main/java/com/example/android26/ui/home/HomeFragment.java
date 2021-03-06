package com.example.android26.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.android26.MainActivity;
import com.example.android26.R;
import com.example.android26.databinding.FragmentHomeBinding;
import com.example.android26.ui.App;
import com.example.android26.ui.home.HomeAdapter.HomeAdapter;
import com.example.android26.ui.home.HomeAdapter.HomeModel;
import com.example.android26.ui.home.HomeAdapter.Listen;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements Listen {


    private NavController navController;
    private FragmentHomeBinding binding;
    private HomeAdapter homeAdapter;
    private List<HomeModel> list = new ArrayList<>();
    private int id;
    private MainActivity mainActivity = new MainActivity();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        homeAdapter = new HomeAdapter(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        navController = NavHostFragment.findNavController(this);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        App.fillDatabase.fillDao().getAll().observe(getViewLifecycleOwner(), new Observer<List<HomeModel>>() {
            @Override
            public void onChanged(List<HomeModel> homeModelList) {
                homeAdapter.addList(homeModelList);
            }
        });


        binding.rv.setAdapter(homeAdapter);
        click();
        getDataInForm();


        requireActivity().getOnBackPressedDispatcher().
                addCallback(
                        getViewLifecycleOwner(),
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                alert();
                            }
                        });
        return binding.getRoot();
    }

    private void getDataInForm() {
        //????????????????????
        getParentFragmentManager().setFragmentResultListener("key",
                getViewLifecycleOwner(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        String a = result.getString("name");
                        String b = result.getString("number");
                        id = result.getInt("id");

                        HomeModel model = homeAdapter.getModelToId(id);
                        if (model != null) {
                            model.setName(a);
                            model.setNumber(b);
                            App.fillDatabase.fillDao().update(model);
                        } else {
                            App.fillDatabase.fillDao().insert(new HomeModel(a, b));
                        }
                    }
                });
    }

    @Override
    //?????????????????????? ????????????
    public void setDataForForm(HomeModel homeModel, int position) {
        Bundle bundle = new Bundle();

        bundle.putString("name1", homeModel.getName());
        bundle.putString("number1", homeModel.getNumber());
        bundle.putInt("id", homeModel.getId());
        getParentFragmentManager().setFragmentResult("2", bundle);
        navController.navigate(R.id.action_navigation_home_to_profileFragment);

    }

    @Override
    public void del(int position) {

        AlertDialog.Builder adg = new AlertDialog.Builder(binding.getRoot().getContext());
        String positive = "????";
        String negative = "??????";
        adg.setMessage("???? ???????????? ?????????????? ?");
        adg.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HomeModel model = homeAdapter.getHomeByPosition(position);
                App.fillDatabase.fillDao().delete(model);
                homeAdapter.notifyItemChanged(position);


            }
        });
        adg.setNegativeButton(negative, null);
        adg.show();

    }


    public void click() {
        binding.fabAdd.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_home_to_profileFragment);
        });


//
//        binding.sort.setOnClickListener(v -> {
//            homeAdapter.addList(App.fillDatabase.fillDao().getAllBySort());
//            binding.rv.setAdapter(homeAdapter);
//            App.share.saveSort(true);
//        });
//        if (App.share.getSaveSort()){
//            Log.e("TAG", "click: " + App.share.getSaveSort() );
//            homeAdapter.addList(App.fillDatabase.fillDao().getAllBySort());
//            binding.rv.setAdapter(homeAdapter);
//        }
    }

    public void alert() {
        AlertDialog.Builder adg = new AlertDialog.Builder(binding.getRoot().getContext());
        String positive = "????";
        String negative = "??????";
        adg.setMessage("???? ???????????? ?????????? ?");
        adg.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requireActivity().finish();
            }
        });
        adg.setNegativeButton(negative, null);
        adg.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sortAZ) {
            homeAdapter.addList(App.fillDatabase.fillDao().getAllBySort());
            binding.rv.setAdapter(homeAdapter);
            Snackbar.make(requireView(), "???????????????????????? A-??", Snackbar.LENGTH_SHORT).show();

        } else if (item.getItemId() == R.id.sortZA) {
            homeAdapter.addList(App.fillDatabase.fillDao().getAllBySortRes());
            binding.rv.setAdapter(homeAdapter);
            Snackbar.make(requireView(), "???????????????????????? ??-??", Snackbar.LENGTH_SHORT).show();

        }
        else if (item.getItemId() == R.id.deleteAll){
            App.fillDatabase.fillDao().deleteAll();
        }
        return super.onOptionsItemSelected(item);
    }
}