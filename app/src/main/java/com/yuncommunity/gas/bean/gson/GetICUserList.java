package com.yuncommunity.gas.bean.gson;

import com.yuncommunity.gas.bean.model.UsersListBean;

/**
 * Created by zangyi_shuai_ge on 2017/10/11
 */

public class GetICUserList {


    /**
     * ArrayOfUsersList : {"xmlns":"http://tempuri.org/","xmlns:xsd":"http://www.w3.org/2001/XMLSchema","UsersList":{"address":"金色水岸6-1-602","mobileNo":"","factoryNo":"03","customerNo":"0000000633","customerType":"0","meterTypeNo":"10","telNo":"","certNo":"","customerName":""},"xmlns:xsi":"http://www.w3.org/2001/XMLSchema-instance"}
     */

    private ArrayOfUsersListBean ArrayOfUsersList;

    public ArrayOfUsersListBean getArrayOfUsersList() {
        return ArrayOfUsersList;
    }

    public void setArrayOfUsersList(ArrayOfUsersListBean ArrayOfUsersList) {
        this.ArrayOfUsersList = ArrayOfUsersList;
    }

    public static class ArrayOfUsersListBean {
        /**
         * xmlns : http://tempuri.org/
         * xmlns:xsd : http://www.w3.org/2001/XMLSchema
         * UsersList : {"address":"金色水岸6-1-602","mobileNo":"","factoryNo":"03","customerNo":"0000000633","customerType":"0","meterTypeNo":"10","telNo":"","certNo":"","customerName":""}
         * xmlns:xsi : http://www.w3.org/2001/XMLSchema-instance
         */
        private UsersListBean UsersList;

        public UsersListBean getUsersList() {
            return UsersList;
        }

        public void setUsersList(UsersListBean UsersList) {
            this.UsersList = UsersList;
        }
    }
}
