package net.twisterrob.colorfilters.android.matrix;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import net.twisterrob.colorfilters.android.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class OrderComponent extends Component {
    private static final String PREF_ORDER_MAP = "Order.map/";
    private static final int[] IDENT = new int[]{0, 1, 2, 3, 4};
    private final ViewGroup[] places;
    private final View[] comps;
    private final int[] map = new int[IDENT.length];

    public OrderComponent(View view, RefreshListener listener) {
        super(view, listener);

        places = new ViewGroup[]{
                vg(R.id.controls_comp_1),
                vg(R.id.controls_comp_2),
                vg(R.id.controls_comp_3),
                vg(R.id.controls_comp_4),
                vg(R.id.controls_comp_5)
        };
        comps = new View[]{
                v(R.id.controls_comp_rR),
                v(R.id.controls_comp_rG),
                v(R.id.controls_comp_rB),
                v(R.id.controls_comp_S),
                v(R.id.controls_comp_sat)
        };
    }

    @Override
    public void setupUI() {
        for (ViewGroup place : places) {
            place.setOnDragListener(new ItemDragListener(new ItemDragListener.ChangeListener() {
                @Override
                public void dropped(ViewGroup source, ViewGroup target) {
                    View from = source.getChildAt(0);
                    View to = target.getChildAt(0);
                    source.removeView(from);
                    target.removeView(to);
                    source.addView(to);
                    target.addView(from);
                    refreshModel();
                    dispatchRefresh(true);
                }
            }));
        }
        for (View comp : comps) {
            comp.setOnTouchListener(new DragStartListener());
        }
    }


    @Override
    public void saveToPreferences(SharedPreferences.Editor editor) {
        for (int i = 0; i < map.length; ++i) {
            editor.putInt(PREF_ORDER_MAP + i, map[i]);
        }
    }

    @Override
    public void restoreFromPreferences(SharedPreferences prefs) {
        for (int i = 0; i < map.length; ++i) {
            map[i] = prefs.getInt(PREF_ORDER_MAP + i, i);
        }
        setMap(map);
    }

    @Override
    public void reset() {
        setMap(IDENT);
    }

    @Override
    public void refreshModel() {
        for (int origPos = 0; origPos < comps.length; ++origPos) {
            int id = comps[origPos].getId();
            for (int newPos = 0; newPos < places.length; ++newPos) {
                View found = places[newPos].findViewById(id);
                if (found != null) {
                    map[newPos] = origPos;
                }
            }
        }
    }

    @Override
    public void combineInto(ColorMatrix colorMatrix) {
        // no touchy
    }

    @Override
    public boolean appendTo(StringBuilder sb) {
        // no visual representation, the order of other components will reflect this
        return false;
    }

    /**
     * @param components must match the order in {@link #comps}.
     */
    public Component[] order(Component... components) {
        if (components.length != map.length) {
            throw new IllegalArgumentException("Invalid number of components");
        }
        Component[] ordered = new Component[components.length];
        for (int i = 0; i < components.length; ++i) {
            ordered[i] = components[map[i]];
        }
        return ordered;
    }

    public int[] getMap() {
        return map;
    }

    public void setMap(int[] map) {
        System.arraycopy(map, 0, this.map, 0, this.map.length);
        refreshUI();
    }

    private void refreshUI() {
        for (ViewGroup place : places) {
            place.removeView(place.getChildAt(0));
        }
        for (int i = 0; i < comps.length; ++i) {
            places[i].addView(comps[map[i]]);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private static class DragStartListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                return view.startDrag(null, new View.DragShadowBuilder(view), view, 0);
            }
            return false;
        }
    }

    private static class ItemDragListener implements View.OnDragListener {
        public interface ChangeListener {
            void dropped(ViewGroup source, ViewGroup target);
        }

        private final ChangeListener listener;

        public ItemDragListener(ChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onDrag(View dropTarget, DragEvent event) {
            if (event.getAction() != DragEvent.ACTION_DRAG_LOCATION) {
                //Log.d("DRAG", describeDragEvent(dropTarget, event));
            }
            final View dragged = (View) event.getLocalState();

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    dragged.post(new Runnable() {
                        public void run() {
                            dragged.setVisibility(View.INVISIBLE);
                        }
                    });
                    setBackground(dropTarget, R.drawable.matrix_order_drop);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    setBackground(dropTarget, R.drawable.matrix_order_drop_active);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    setBackground(dropTarget, R.drawable.matrix_order_drop);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    dragged.post(new Runnable() {
                        public void run() {
                            dragged.setVisibility(View.VISIBLE);
                        }
                    });
                    setBackground(dropTarget, R.drawable.matrix_order_component);
                    break;
                case DragEvent.ACTION_DROP:
                    ViewGroup source = (ViewGroup) dragged.getParent();
                    ViewGroup target = (ViewGroup) dropTarget;
                    if (source == target) break;
                    listener.dropped(source, target);
                    break;
            }
            return true;
        }

        @SuppressWarnings("deprecation")
        private void setBackground(View dropTarget, int drawableResourceID) {
            Drawable drawable = dropTarget.getResources().getDrawable(drawableResourceID);
            dropTarget.setBackgroundDrawable(drawable);
        }
    }
}
