import React from 'react';
import ReferenceForm from '../ReferenceForm'


const EditUser = () => {

  let params = {};

  params.submitAction = "editUser"
  params.formTitle = "Edit User"

  return ReferenceForm(params);

}

export default EditUser;




