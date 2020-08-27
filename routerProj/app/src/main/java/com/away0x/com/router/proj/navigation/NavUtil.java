package com.away0x.com.router.proj.navigation;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.DialogFragmentNavigator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.away0x.com.router.proj.R;
import com.away0x.com.router.proj.navigation.HiFragmentNavigator;
import com.away0x.com.router.proj.navigation.model.BottomBar;
import com.away0x.com.router.proj.navigation.model.Destination;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NavUtil {

    private static HashMap<String, Destination> destinations;

    /** 解析 assets/destination.json 中的路由配置为 NavGraph，设置到 NavController 中 */
    public static void builderNavGraph(FragmentActivity activity, FragmentManager childFm, NavController controller, int containerId) {
        String content = parseFile(activity, "destination.json");
        destinations = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {});

        Iterator<Destination> iterator = destinations.values().iterator();
        NavigatorProvider provider = controller.getNavigatorProvider();

        NavGraphNavigator graphNavigator = provider.getNavigator(NavGraphNavigator.class);
        NavGraph navGraph = new NavGraph(graphNavigator);

        // 替换 Navigator，为自定义的 Navigator，使 路由 fragment 切换从 replace 替换成 show-hide 模式
        HiFragmentNavigator hiFragmentNavigator = new HiFragmentNavigator(activity, childFm, containerId);
        provider.addNavigator(hiFragmentNavigator);

        while (iterator.hasNext()) {
            Destination destination = iterator.next();

            switch (destination.destType) {
                case "activity": {
                    ActivityNavigator navigator = provider.getNavigator(ActivityNavigator.class);
                    ActivityNavigator.Destination node = navigator.createDestination();
                    node.setId(destination.id);
                    node.setComponentName(new ComponentName(activity.getPackageName(), destination.clazName));
                    navGraph.addDestination(node);
                    break;
                }
                case "fragment": {
                    //FragmentNavigator navigator = provider.getNavigator(FragmentNavigator.class);
                    HiFragmentNavigator.Destination node = hiFragmentNavigator.createDestination();
                    //FragmentNavigator.Destination node = navigator.createDestination();
                    node.setId(destination.id);
                    node.setClassName(destination.clazName);

                    navGraph.addDestination(node);

                    break;
                }
                case "dailog": {
                    DialogFragmentNavigator navigator = provider.getNavigator(DialogFragmentNavigator.class);
                    DialogFragmentNavigator.Destination node = navigator.createDestination();

                    node.setId(destination.id);
                    node.setClassName(destination.clazName);

                    navGraph.addDestination(node);
                    break;
                }
            }

            if (destination.asStarter) {
                navGraph.setStartDestination(destination.id);
            }
        }

        controller.setGraph(navGraph);
    }

    /** 解析 assets/main_tabs_config.json，设置底部导航  */
    public static void builderBottomBar(BottomNavigationView navView) {
        String content = parseFile(navView.getContext(), "main_tabs_config.json");
        BottomBar bottomBar = JSON.parseObject(content, BottomBar.class);
        List<BottomBar.Tab> tabs = bottomBar.tabs;
        Menu menu = navView.getMenu();

        for (BottomBar.Tab tab : tabs) {
            if (!tab.enable) continue;

            Destination destination = destinations.get(tab.pageUrl);
            if (destination != null) {
                MenuItem menuItem = menu.add(0, destination.id, tab.index, tab.title);
                menuItem.setIcon(R.drawable.ic_home_black_24dp);
            }
        }
    }

    /** 读取 assets 下的文件内容 */
    private static String parseFile(Context context, String filename) {
        AssetManager assets = context.getAssets();

        try {
            InputStream inputStream = assets.open(filename);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            StringBuilder builder = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }

            inputStream.close();
            bufferedReader.close();

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
