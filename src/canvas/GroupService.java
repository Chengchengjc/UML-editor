package canvas;

import java.util.ArrayList;

import element.BasicObject;
import element.GroupObject;

class GroupService {
    private ArrayList<BasicObject> objects;

    public GroupService(ArrayList<BasicObject> objects) {
        this.objects = objects;
    }

    public GroupObject groupSelectedObjects() {
        ArrayList<BasicObject> selectedObjects = new ArrayList<>();

        for (int i = 0; i < objects.size(); i++) {
            BasicObject obj = objects.get(i);
            if (obj.isSelected())
                selectedObjects.add(obj);
        }

        if (selectedObjects.size() <= 1)
            return null;
        GroupObject groupObject = new GroupObject(selectedObjects);
        for (BasicObject obj : objects)
            obj.setSelected(false);

        // group obj移出canvas objects，改由group object形式放入
        for (BasicObject obj : selectedObjects)
            objects.remove(obj);
        objects.add(groupObject);
        groupObject.setSelected(true);

        return groupObject;
    }

    public boolean ungroupSelectedObjects() {
        int selectedCount = 0;
        BasicObject groupToUngroup = null;
        for (int i = 0; i < objects.size(); i++) {
            BasicObject obj = objects.get(i);
            if (obj.isSelected()) {
                selectedCount++;
                groupToUngroup = obj;
            }
            if (selectedCount > 1)
                return false; // 只能取消群組一個物件，選到多於一個物件就不做事
        }

        objects.remove(groupToUngroup);
        if (groupToUngroup instanceof GroupObject) {
            GroupObject groupObj = (GroupObject) groupToUngroup;
            for (BasicObject obj : groupObj.getMembers()) {
                objects.add(obj);
                obj.setSelected(false);
            }
        } else { // 不是群組物件就直接放回去
            objects.add(groupToUngroup);
        }

        return true;
    }
}
