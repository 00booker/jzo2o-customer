package com.jzo2o.customer.controller.consumer;


import com.jzo2o.api.customer.dto.response.AddressBookResDTO;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.model.dto.PageQueryDTO;
import com.jzo2o.customer.model.dto.request.AddressBookUpsertReqDTO;
import com.jzo2o.customer.service.IAddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController(value = "addressbook")
@RequestMapping(value = "/consumer/address-book")
@Api(tags = "用户端 - 普通用户地址簿相关接口")
public class AddressBookController {

    @Resource
    private IAddressBookService addressBookService;
    @PostMapping
    @ApiOperation(value = "新增地址簿")
    public void addAddressBook(@RequestBody AddressBookUpsertReqDTO addressBookUpsertReqDTO) {
        addressBookService.addAddressBook(addressBookUpsertReqDTO);
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询地址簿")
    public PageResult<AddressBookResDTO> getByUserIdAndCity(PageQueryDTO pageQueryDTO) {
        return addressBookService.pageQueryAddressBook(pageQueryDTO);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询地址簿")
    public AddressBookResDTO getById(@PathVariable Long id) {
        return addressBookService.getAddressBookById(id);
    }
    @PutMapping("/{id}")
    @ApiOperation(value = "根据id查询地址簿")
    public void updateById(@PathVariable Long id, @RequestBody AddressBookUpsertReqDTO addressBookUpsertReqDTO) {
        addressBookService.updateById(id, addressBookUpsertReqDTO);
    }

    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除地址簿")
    public void deleteByIds(@RequestBody String[] ids) {
        addressBookService.removeAddressBookByIds(ids);
    }

    @PutMapping("/default")
    @ApiOperation(value = "设置取消默认地址")
    public void setDefault(@RequestParam(value = "flag") Integer flag,  @RequestParam(value = "id") Long id) {
        addressBookService.setDefault(flag, id);
    }

    @GetMapping("/defaultAddress")
    @ApiOperation(value = "获取默认地址")
    public AddressBookResDTO getDefaultAddress() {
        return addressBookService.getDefaultAddress();
    }


}
