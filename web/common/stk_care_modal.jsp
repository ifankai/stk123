<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal" id="stk-care-modal" tabindex="-1" role="dialog" style="display:none;">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">股票关注</h4>
      </div>
      <div class="modal-body form-horizontal care">
          <div class="control-group">
            <label for="recipient-name" class="control-label">股票：</label>
            <input type="text" class="form-control" id="name" value="">
            <input type="hidden" class="form-control" id="code" value="">
          </div>
          <div class="control-group">
            <label for="recipient-name" class="control-label">信息：</label>
            <input type="text" class="form-control" id="info" value="">
            <input type="hidden" class="form-control" id="url" value="">
          </div>
          <div class="control-group">
            <label for="recipient-name" class="control-label">信息类型：</label>
            <input type="text" class="form-control" id="type" value="">
          </div>
          <div class="control-group">
            <label for="recipient-name" class="control-label">创建时间：</label>
            <input type="text" class="form-control" id="createtime" value="">
          </div>
          <div class="control-group">
            <label for="message-text" class="control-label">备注：</label>
            <textarea class="form-control" id="memo"></textarea>
          </div>
          <div class="control-group">
            <label for="recipient-name" class="control-label">参数1：</label>
            <input type="text" class="form-control" id="param1" value="">
          </div>
          <div class="control-group">
            <label for="recipient-name" class="control-label">参数2：</label>
            <input type="text" class="form-control" id="param2" value="">
          </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="care-btn">保存</button>
        <button type="button" class="btn btn-default" data-dismiss="modal" id="care-cancel-btn">取消</button>
      </div>
    </div>
  </div>
</div>